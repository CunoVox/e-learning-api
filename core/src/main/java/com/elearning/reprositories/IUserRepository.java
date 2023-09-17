package com.elearning.reprositories;

import com.elearning.entities.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
    Optional<User> findById(String id);
    User findByFullName(String name);
    User findByEmailAndIsEmailConfirmedIsTrue(String email);

}
