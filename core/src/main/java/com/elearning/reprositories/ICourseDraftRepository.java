package com.elearning.reprositories;

import com.elearning.entities.CourseDraft;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ICourseDraftRepository extends MongoRepository<CourseDraft, String>, ICourseDraftRepositoryCustom {
}
