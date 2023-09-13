package com.elearning.reprositories;

import com.elearning.entities.SequenceValueItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ISequenceValueItemRepository extends MongoRepository<SequenceValueItem, String>, ISequenceValueItemRepositoryCustom {

    SequenceValueItem findBySeqName(String sequenceName);

}
