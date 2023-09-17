package com.elearning.reprositories;

import com.elearning.entities.Category;
import com.elearning.models.searchs.ParameterSearchCategory;

import java.util.List;

public interface ICategoryRepositoryCustom {
    List<Category> searchCategories(ParameterSearchCategory parameterSearchCategory);
}
