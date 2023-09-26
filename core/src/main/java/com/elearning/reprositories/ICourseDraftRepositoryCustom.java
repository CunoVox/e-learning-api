package com.elearning.reprositories;

import com.elearning.entities.CourseDraft;
import com.elearning.models.searchs.ParameterSearchCourseDraft;

import java.util.List;

public interface ICourseDraftRepositoryCustom {
    List<CourseDraft> searchCourseDraft(ParameterSearchCourseDraft parameterSearchCourseDraft);
}
