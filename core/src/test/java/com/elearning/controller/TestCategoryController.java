package com.elearning.controller;

import com.elearning.models.dtos.CategoryDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan({"com.elearning"})
public class TestCategoryController {
    @Autowired
    CategoryController categoryController;
    @Test
    public void createCategory(){
        CategoryDTO category = CategoryDTO.builder().title("Danh mục").parentId("1005").build();
        CategoryDTO categoryDTO = categoryController.createCategory(category, "haohao");
        Assertions.assertNotNull(categoryDTO);
    }
}
