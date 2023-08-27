package com.elearning.controller;

import com.elearning.entities.Product;
import com.elearning.reprositories.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    IProductRepository iProductRepository;

    public Product test() {
        Product product = Product.builder().name("dmm").title("cc").build();
        Product product1 = iProductRepository.save(product);
        return product;
    }
}
