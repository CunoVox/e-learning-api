package com.elearning.reprositories;

import com.elearning.entities.Course;
import com.elearning.entities.Enrollment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IEnrollmentRepository extends MongoRepository<Enrollment, String>, IEnrollmentRepositoryCustom {
    Enrollment findByCourseIdAndUserId(String courseId, String userId);
    List<Enrollment> findAllByUserId(String userId);
}
