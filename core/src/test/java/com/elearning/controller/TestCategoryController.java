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
        CategoryDTO category = CategoryDTO.builder().title("Áo nam").parentId("1007").createdBy("haohao").build();
        CategoryDTO categoryDTO = categoryController.createCategory(category);
        Assertions.assertNotNull(categoryDTO);
    }

    @Test
    public void testGetCategoryById(){
        CategoryDTO categoryDTO = categoryController.getCategoryById("1005");
        Assertions.assertNotNull(categoryDTO);
    }

    @Test
    public void testUpdateCategory(){
        CategoryDTO category = CategoryDTO.builder().id("1007")
                .title("Quần Áo Nam")
                .parentId("1005")
                .level(3)
                .createdBy("haohao")
                .updateBy("lamlam")
                .build();
        CategoryDTO categoryDTO = categoryController.updateCategory(category);
        Assertions.assertNotNull(categoryDTO);
    }
}
