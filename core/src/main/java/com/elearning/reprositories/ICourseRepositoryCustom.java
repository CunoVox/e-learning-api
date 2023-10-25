package com.elearning.reprositories;

import com.elearning.entities.Course;
import com.elearning.models.searchs.ParameterSearchCourse;
import com.elearning.models.wrapper.ListWrapper;

import java.util.List;

public interface ICourseRepositoryCustom {
    ListWrapper<Course> searchCourse(ParameterSearchCourse parameterSearchCourse);
    void updateCourseType(String courseId, String courseType, String updateBy);
}
