package com.elearning.apis;

import com.elearning.annotation.validator.ValuesAllowed;
import com.elearning.controller.CategoryController;
import com.elearning.models.dtos.CategoryDTO;
import com.elearning.models.searchs.ParameterSearchCategory;
import com.elearning.utils.Extensions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category API")
@ExtensionMethod(Extensions.class)
public class CategoryAPI {
    @Autowired
    CategoryController categoryController;

    @Operation(summary = "Danh sách danh mục")
    @GetMapping
    public List<CategoryDTO> getCategories(@RequestParam(value = "type_build")
                                           @ValuesAllowed(values = {"list", "tree"}) @Parameter(description = "Allow: ['list','tree']\nDefault: 'tree'") String typeBuild,
                                           @RequestParam(value = "level", required = false) Integer level,
                                           @RequestParam(value = "is_deleted", required = false) Boolean isDeleted,
                                           @RequestParam(value = "build_courses", required = false) Boolean buildCourses,
                                           @RequestParam(value = "categories_ids", required = false) List<String> categoriesIds,
                                           @RequestParam(value = "parent_ids", required = false) List<String> parentIds) {
        ParameterSearchCategory parameterSearchCategory = new ParameterSearchCategory();
        if (!typeBuild.isBlankOrNull()){
            parameterSearchCategory.setTypeBuild(typeBuild);
        }
        if (level!=null){
            parameterSearchCategory.setLevel(level);
        }
        if (isDeleted!=null){
            parameterSearchCategory.setIsDeleted(isDeleted);
        }
        if (buildCourses!=null){
            parameterSearchCategory.setBuildCourses(buildCourses);
        }
        if (!categoriesIds.isNullOrEmpty()){
            parameterSearchCategory.setCategoriesIds(categoriesIds);
        }
        if (!parentIds.isNullOrEmpty()){
            parameterSearchCategory.setParentIds(parentIds);
        }
        return categoryController.searchCategoryDTOS(parameterSearchCategory);
    }

}
