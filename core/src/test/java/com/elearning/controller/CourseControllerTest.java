package com.elearning.controller;

import com.elearning.reprositories.ICourseRepository;
import com.elearning.reprositories.IRatingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@SpringBootTest
@ComponentScan({"com.elearning"})
public class CourseControllerTest {
    @Autowired
    private ICourseRepository courseRepository;
    @Autowired
    private IRatingRepository ratingRepository;

//    @Test
//    public void testCountAllByCreatedByAndCourseTypeInAndLevelAndIsAndIsDeletedNotIn() {
////        courseRepository.sumSubscriptionsByCreatedBy("1003");
////        courseRepository.   countAllByCreatedByAndCourseTypeInAndLevelAndIsDeletedNotIn("1003", List.of("OFFICIAL", "CHANGE_PRICE"), 1, List.of(true));
//        courseRepository.countAllByCreatedBy(List.of("1003"));
//    }
    @Test
    public void testFindAllByCreatedByInAndLevel() {
        ratingRepository.avgRattingByCourseCreatedByIn(List.of("1003"));
    }
}
