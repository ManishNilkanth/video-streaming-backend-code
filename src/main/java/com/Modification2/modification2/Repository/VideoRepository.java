package com.Modification2.modification2.Repository;


import com.Modification2.modification2.module.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepository extends MongoRepository<Video, String> {
}

