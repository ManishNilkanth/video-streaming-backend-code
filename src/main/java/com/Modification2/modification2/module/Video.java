package com.Modification2.modification2.module;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import com.fasterxml.jackson.databind.deser.impl.InnerClassProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "Video")
public class Video {
    @Id

    private String id;
    private String title;
    private String description;
    private String userId;
    private AtomicInteger likes = new AtomicInteger(0);
    private  AtomicInteger disLikes = new AtomicInteger(0);

    private Set<String> tags;
    private String videoUrl;
    private VideoStatus videoStatus;
    private AtomicInteger viewCount =new AtomicInteger(0);
    private String thumbnailUrl;
    private List<Comment> commentList= new CopyOnWriteArrayList<>();

    public void incrementLikes()
    {
        likes.incrementAndGet();
    }

    public void decrementLikes()
    {
        likes.decrementAndGet();
    }
    public void incrementDislikes()
    {
        disLikes.incrementAndGet();
    }
    public void decrementDisLikes()
    {
        disLikes.decrementAndGet();
    }

    public void incrementVideoCount() {
        viewCount.incrementAndGet();
    }

    public void addComment(Comment comment) {
        commentList.add(comment);
    }
}
