package com.elearning.reprositories;

import com.elearning.entities.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IProductRepository extends MongoRepository<Product, String> {

}
