package com.elearning.reprositories;

import com.elearning.entities.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
    User findByFullName(String name);

}
