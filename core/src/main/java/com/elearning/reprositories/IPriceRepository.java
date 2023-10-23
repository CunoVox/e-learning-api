package com.elearning.reprositories;

import com.elearning.entities.Price;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IPriceRepository extends MongoRepository<Price, String> {
    Price findByParentIdAndType(String parentId, String type);
}
