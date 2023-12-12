package com.elearning.reprositories;

import com.elearning.entities.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IRatingRepository extends MongoRepository<Rating, String> {
    Optional<Rating> findById(String id);
    Optional<Rating> findByCourseIdAndUserId(String courseId, String userId);
    List<Rating> findAllByCourseId(String courseId);
}
