package com.elearning.reprositories;

import com.elearning.entities.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface ICourseRepository extends MongoRepository<Course, String>, ICourseRepositoryCustom {
    List<Course> findAllByParentIdInAndIsDeletedNotIn(Collection<String> parentId, Collection<Boolean> isDeleted);

    List<Course> findByIdIn(List<String> courseIds);

    List<Course> findAllByCreatedByInAndLevel(Collection<String> createdBy, int level);
}
