package com.Modification2.modification2.service;


import com.Modification2.modification2.Dto.CommentDto;
import com.Modification2.modification2.Dto.UploadVideoResponse;
import com.Modification2.modification2.Dto.VideoDto;
import com.Modification2.modification2.Repository.VideoRepository;
import com.Modification2.modification2.module.Comment;
import com.Modification2.modification2.module.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final S3Service s3Service;
    private final VideoRepository videoRepository;
   private final UserService userService;

    public UploadVideoResponse uploadVideo(MultipartFile multipartFile)
    {
        //Upload the files to AWS S3
        //Save videos data to database
        String videoUrl =s3Service.uploadFile(multipartFile);
        var video = new Video();
        video.setVideoUrl(videoUrl);

        var  savedVideo= videoRepository.save(video);
        return new UploadVideoResponse(savedVideo.getId(),savedVideo.getVideoUrl());
    }

    public VideoDto editVideo(VideoDto videoDto)
    {
        //find the video by video Id
        var savedVideo =  getVideoById(videoDto.getId());

        //Map the videoDto fields to video
        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setVideoStatus(videoDto.getVideoStatus());
        //save the video to the database
        videoRepository.save(savedVideo);
        return videoDto;
    }

    public String uploadThumbnail(MultipartFile file,String videoId)
    {
        var savedVideo =getVideoById(videoId);

        String thumbnailUrl= s3Service.uploadFile(file);
        videoRepository.save(savedVideo);
        return thumbnailUrl;
    }
    Video getVideoById(String videoId)
    {
        return videoRepository.findById(videoId)
                .orElseThrow(() ->new IllegalArgumentException("cannot find video by id-"+videoId));

    }

    public VideoDto getVideoDetails(String videoId) {
        Video savedVideo = getVideoById(videoId);

        increaseVideoCount(savedVideo);
//       userService.addVideoToHistory(videoId);     // commentOut it to run the program
       return mapToVideoDto(savedVideo);

    }

    private void increaseVideoCount(Video savedVideo) {
        savedVideo.incrementVideoCount();
        videoRepository.save(savedVideo);
    }

    public VideoDto likeVideo(String videoId) {

        //video by id
        Video videoById =getVideoById(videoId);

        //increment like count
        //if user already liked the video, the decrement like count

        // if user already dislike the video ,then increment like count and decrement dislike count

        if(userService.ifLikedVideo(videoId))
        {
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
        }
        else if(userService.ifDisLikedVideo(videoId)) {
            videoById.decrementDisLikes();
            userService.removeFromDisLikedVideos(videoId);
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }
        else
        {
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }
        videoRepository.save(videoById);

      return mapToVideoDto(videoById);

    }

    public VideoDto disLikeVideo(String videoId) {

        Video videoById =getVideoById(videoId);

        if(userService.ifDisLikedVideo(videoId))
        {
            videoById.decrementDisLikes();
            userService.removeFromDisLikedVideos(videoId);
        }
        else if(userService.ifLikedVideo(videoId)) {
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
            videoById.incrementDislikes();
            userService.addToDisLikedVideos(videoId);
        }
        else
        {
            videoById.incrementDislikes();
            userService.addToDisLikedVideos(videoId);
        }
        videoRepository.save(videoById);

       return mapToVideoDto(videoById);

    }

    private VideoDto mapToVideoDto(Video videoById) {
        VideoDto videoDto = new VideoDto();
        videoDto.setVideoUrl(videoById.getVideoUrl());
        videoDto.setThumbnailUrl(videoById.getThumbnailUrl());
        videoDto.setId(videoById.getId());
        videoDto.setTitle(videoById.getTitle());
        videoDto.setVideoStatus(videoById.getVideoStatus());
        videoDto.setDescription(videoById.getDescription());
        videoDto.setTags(videoById.getTags());
        videoDto.setLikeCount(videoById.getLikes().get());
        videoDto.setDislikeCount(videoById.getDisLikes().get());
        videoDto.setViewCount(videoById.getViewCount().get());
        return videoDto;
    }

    public void addComment(String videoId, CommentDto commentDto) {
       Video video = getVideoById(videoId);

        Comment comment = new Comment();
        comment.setText(commentDto.getCommentText());
        comment.setAuthorId(commentDto.getAuthorId());
        video.addComment(comment);

        videoRepository.save(video);
    }

    public List<CommentDto> getAllComments(String videoId) {
        Video video =getVideoById(videoId);
        List<Comment> commentList = video.getCommentList();

      return   commentList.stream().map(this::mapToCommentDto).toList();
    }

    private CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentText(comment.getText());
        commentDto.setAuthorId(comment.getAuthorId());
        return commentDto;
    }


    public List<VideoDto> getAllVideos() {
        return  videoRepository.findAll().stream().map(this::mapToVideoDto).toList();
    }
}

