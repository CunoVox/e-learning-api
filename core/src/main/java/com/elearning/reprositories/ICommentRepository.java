package com.elearning.reprositories;

import com.elearning.entities.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ICommentRepository extends MongoRepository<Comment, String>, ICommentRepositoryCustom  {
    List<Comment> findAllByReferenceIdAndType(String courseId, String type);

}
