package com.elearning.reprositories;

import com.elearning.entities.ConfigProperty;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IConfigPropertyRepository extends MongoRepository<ConfigProperty, String> {
    ConfigProperty findByName(String name);
}
