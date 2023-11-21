package com.elearning.reprositories;

import com.elearning.entities.Enrollment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IEnrollmentRepository extends MongoRepository<Enrollment, String>, IEnrollmentRepositoryCustom {
    Enrollment findByCourseIdAndUserId(String courseId, String userId);
    Optional<Enrollment> findById(String id);
    List<Enrollment> findAllByUserId(String userId);
}
