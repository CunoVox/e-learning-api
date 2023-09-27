//package com.elearning.controller;
//
//import com.elearning.connector.Connector;
//import com.elearning.utils.enumAttribute.EnumConnectorType;
//import com.elearning.utils.enumAttribute.EnumRelatedObjectsStatus;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.ComponentScan;
//
//import java.util.List;
//import java.util.Map;
//
//@SpringBootTest
//@ComponentScan({"com.elearning"})
//public class TestConnector {
//    @Autowired
//    Connector connector;
//    @Test
//    public void testCreateConnector(){
//        courseManager.addRelatedObjectById(
//                "course",
//                "2",
//                "category",
//                "3",
//                EnumRelatedObjectsStatus.ACTIVE.getValue(),
//                EnumConnectorType.COURSE_TO_TAG.name(),
//                "haohao");
////        Map<String, List<String>> mapTagIds = connector.getIdRelatedObjectsById(
////                "course",
////                List.of("1", "2"),
////                "tag",
////                EnumConnectorType.COURSE_TO_TAG.name());
////        List<String> categories = connector.getIdRelatedObjectsById("category", "1", "course",EnumConnectorType.COURSE_TO_CATEGORY.name());
////        System.out.println(mapTagIds);
//    }
//}
