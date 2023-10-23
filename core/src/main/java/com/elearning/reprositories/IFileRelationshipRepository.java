package com.elearning.reprositories;

import com.elearning.entities.FileRelationship;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IFileRelationshipRepository extends MongoRepository<FileRelationship, String> {
    FileRelationship findByParentIdAndParentTypeAndFileType(String parentId, String parentType, String fileType);
}
