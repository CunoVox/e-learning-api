package com.elearning.reprositories.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public abstract class BaseRepositoryCustom {

    @Autowired
    protected MongoTemplate mongoTemplate;

}
