package com.Modification2.modification2.Repository;

import com.Modification2.modification2.module.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository  extends MongoRepository<User,String> {
    Optional<User> findBySub(String sub);
}
