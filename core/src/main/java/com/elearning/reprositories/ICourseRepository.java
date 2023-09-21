package com.elearning.reprositories;

import com.elearning.entities.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ICourseRepository extends MongoRepository<Course, String>, ICourseRepositoryCustom {

}
