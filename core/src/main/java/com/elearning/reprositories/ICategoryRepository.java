package com.elearning.reprositories;

import com.elearning.entities.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ICategoryRepository extends MongoRepository<Category, String>, ICategoryRepositoryCustom {
    List<Category> findAllByNameModeLike(String nameMod);
}
