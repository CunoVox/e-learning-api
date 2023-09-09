//package com.elearning.controller;
//
//import com.elearning.manager.CourseManager;
//import com.elearning.utils.enumAttribute.EnumConnectorType;
//import com.elearning.utils.enumAttribute.EnumRelatedObjectsStatus;
//import com.elearning.utils.enumAttribute.EnumRelatedObjectsWeight;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.ComponentScan;
//
//@SpringBootTest
//@ComponentScan({"com.elearning"})
//public class TestConnector {
//    @Autowired
//    CourseManager courseManager;
//    @Test
//    public void testCreateConnector(){
//        courseManager.addRelatedObjectById(
//                "2222",
//                "tag",
//                "1111",
//                EnumRelatedObjectsWeight.MEDIUM.getValue(),
//                EnumRelatedObjectsStatus.ACTIVE.getValue(),
//                EnumConnectorType.COURSE_TO_TAG.name(),
//                "haohao");
//    }
//}
