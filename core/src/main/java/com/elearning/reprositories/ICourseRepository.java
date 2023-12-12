package com.elearning.reprositories;

import com.elearning.entities.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ICourseRepository extends MongoRepository<Course, String>, ICourseRepositoryCustom {
    List<Course> findAllByParentIdInAndIsDeletedNotIn(Collection<String> ids, boolean isDeleted);

}
