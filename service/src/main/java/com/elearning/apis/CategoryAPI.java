package com.elearning.apis;

import com.elearning.annotation.validator.ValuesAllowed;
import com.elearning.controller.CategoryController;
import com.elearning.models.dtos.CategoryDTO;
import com.elearning.models.searchs.ParameterSearchCategory;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumCategoryBuildType;
import io.swagger.annotations.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
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
    public List<CategoryDTO> getCategories(@RequestParam(value = "buildType") EnumCategoryBuildType buildType,
                                           @RequestParam(value = "level", required = false) Integer level,
                                           @RequestParam(value = "is_deleted", required = false) Boolean isDeleted,
                                           @RequestParam(value = "build_courses", required = false) Boolean buildCourses,
                                           @RequestParam(value = "categories_ids", required = false) List<String> categoriesIds,
                                           @RequestParam(value = "parent_ids", required = false) List<String> parentIds) {
        ParameterSearchCategory parameterSearchCategory = new ParameterSearchCategory();
        if (buildType != null) {
            parameterSearchCategory.setBuildType(buildType.name());
        }
        if (level != null) {
            parameterSearchCategory.setLevel(level);
        }
        if (isDeleted != null) {
            parameterSearchCategory.setIsDeleted(isDeleted);
        }
        if (buildCourses != null) {
            parameterSearchCategory.setBuildCourses(buildCourses);
        }
        if (!categoriesIds.isNullOrEmpty()) {
            parameterSearchCategory.setCategoriesIds(categoriesIds);
        }
        if (!parentIds.isNullOrEmpty()) {
            parameterSearchCategory.setParentIds(parentIds);
        }
        return categoryController.searchCategoryDTOS(parameterSearchCategory);
    }

    @Operation(summary = "Thêm danh mục")
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CategoryDTO createCategory(@RequestBody CategoryDTO categoryDTO) {
        return categoryController.createCategory(categoryDTO);
    }

    @Operation(summary = "Sửa danh mục")
    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCategory(CategoryDTO categoryDTO) {
        categoryController.updateCategory(categoryDTO);
    }

    @Operation(summary = "Xoá danh mục")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCategory(@PathVariable(value = "id") String id,
                               @RequestParam(value = "delete_by") String deleteBy) {
        categoryController.deleteCategory(id, deleteBy);
    }
}
