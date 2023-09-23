package com.elearning.controller;

import com.elearning.models.dtos.CourseDraftDTO;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.ComponentScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan({"com.elearning"})
public class TestCourseDraftController {
    @Autowired
    CourseDraftController courseDraftController;
    @Test
    public void testCreate(){
        CourseDraftDTO dto = CourseDraftDTO.builder().name("Khóa 1").createdBy("me").build();
        CourseDraftDTO courseDraftDTO = courseDraftController.createCourseDraft(dto);
        Assertions.assertNotNull(courseDraftDTO);

//        CategoryDTO categoryDTO = categoryController.createCategory(category);
    }
}
