package com.elearning.controller;

import com.elearning.entities.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan({"com.elearning"})
public class Tests {
    @Autowired
    ProductController productController;

    @Test
    public void testss(){
        Product product1 = productController.test();
        Assertions.assertNotNull(product1);
    }
}
