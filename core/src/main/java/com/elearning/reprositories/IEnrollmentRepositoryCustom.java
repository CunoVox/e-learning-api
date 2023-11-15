package com.elearning.reprositories;

import com.elearning.entities.Enrollment;
import com.elearning.models.searchs.ParameterSearchEnrollment;
import com.elearning.models.wrapper.ListWrapper;

public interface IEnrollmentRepositoryCustom {
    ListWrapper<Enrollment> searchEnrollment(ParameterSearchEnrollment parameterSearchEnrollment);

}
