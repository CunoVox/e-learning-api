package com.elearning.reprositories;

import com.elearning.entities.Course;
import com.elearning.models.searchs.ParameterSearchCourse;

import java.util.List;

public interface ICourseRepositoryCustom {
    List<Course> searchCourse(ParameterSearchCourse parameterSearchCourse);
    void updateCourseType(String courseId, String courseType, String updateBy);
}
