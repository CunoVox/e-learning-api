//package com.elearning.controller;
//
//import com.elearning.models.dtos.CourseDraftDTO;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.ComponentScan;
//
//@SpringBootTest
//@ComponentScan({"com.elearning"})
//public class TestCourseDraftController {
//    @Autowired
//    CourseDraftController courseDraftController;
//    @Test
//    public void testCreate(){
//        CourseDraftDTO dto = CourseDraftDTO.builder().name("Khóa 1").createdBy("me").build();
//        CourseDraftDTO courseDraftDTO = courseDraftController.createCourseDraft(dto);
//        Assertions.assertNotNull(courseDraftDTO);
//
////        CategoryDTO categoryDTO = categoryController.createCategory(category);
//    }
//}
