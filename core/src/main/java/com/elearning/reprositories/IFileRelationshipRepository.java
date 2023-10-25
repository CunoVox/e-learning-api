package com.elearning.reprositories;

import com.elearning.entities.FileRelationship;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface IFileRelationshipRepository extends MongoRepository<FileRelationship, String> {
    List<FileRelationship> findAllByParentIdInAndParentType(Collection<String> parentIds, String parentType);
}
