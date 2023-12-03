//package com.elearning.controller;
//
//import com.elearning.connector.Connector;
//import com.elearning.entities.Category;
//import com.elearning.utils.enumAttribute.EnumConnectorType;
//import com.elearning.utils.enumAttribute.EnumRelatedObjectsStatus;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.util.List;
//import java.util.Map;
//
//@SpringBootTest
//@ComponentScan({"com.elearning"})
//public class TestConnector {
//    @Autowired
//    Connector connector;
//
//    //Viết hàm deleteConnector
//    @Test
//    public void testDeleteConnector() {
//        connector.deleteConnector("course", "1237", "category", EnumConnectorType.COURSE_TO_CATEGORY.name());
//    }
//
//}
