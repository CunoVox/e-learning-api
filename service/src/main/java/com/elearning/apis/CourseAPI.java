package com.elearning.apis;

import com.elearning.controller.CourseController;
import com.elearning.models.dtos.CategoryDTO;
import com.elearning.models.dtos.CourseDTO;
import com.elearning.models.searchs.ParameterSearchCourse;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumCourseType;
import com.elearning.utils.enumAttribute.EnumListBuildType;
import io.swagger.v3.oas.annotations.Operation;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
@ExtensionMethod(Extensions.class)
public class CourseAPI {
    @Autowired
    private CourseController courseController;

    private ParameterSearchCourse buildParameterSearch(
            @RequestParam(value = "build_type") EnumListBuildType buildType,
            @RequestParam(value = "search_type") EnumCourseType searchType,
            @RequestParam(value = "level", required = false) Integer level,
            @RequestParam(value = "is_deleted", required = false) Boolean isDeleted,
            @RequestParam(value = "build_courses", required = false) Boolean buildCourses,
            @RequestParam(value = "categories_ids", required = false) List<String> categoriesIds,
            @RequestParam(value = "parent_ids", required = false) List<String> parentIds) {

        ParameterSearchCourse parameterSearchCourse = new ParameterSearchCourse();

        if (buildType != null) {
            parameterSearchCourse.setBuildType(buildType.name());
        }
        if (searchType != null) {
            parameterSearchCourse.setSearchType(searchType.name());
        }
        if (level != null) {
            parameterSearchCourse.setLevel(level);
        }
        if (isDeleted != null) {
            parameterSearchCourse.setIsDeleted(isDeleted);
        }
        if (buildCourses != null) {
            parameterSearchCourse.setBuildCourses(buildCourses);
        }
        if (!categoriesIds.isNullOrEmpty()) {
            parameterSearchCourse.setIds(categoriesIds);
        }
        if (!parentIds.isNullOrEmpty()) {
            parameterSearchCourse.setParentIds(parentIds);
        }
        return parameterSearchCourse;
    }

    @GetMapping("/")
    @Operation(description = "Danh sách khoá học")
    public List<CourseDTO> getCourse(@RequestParam(value = "build_type") EnumListBuildType buildType,
                                     @RequestParam(value = "level", required = false) Integer level,
                                     @RequestParam(value = "search_type", required = false) EnumCourseType searchType,
                                     @RequestParam(value = "is_deleted", required = false) Boolean isDeleted,
                                     @RequestParam(value = "build_courses", required = false) Boolean buildCourses,
                                     @RequestParam(value = "ids", required = false) List<String> ids,
                                     @RequestParam(value = "parent_ids", required = false) List<String> parentIds) {
        ParameterSearchCourse parameterSearchCourse =
                this.buildParameterSearch(buildType, searchType, level, isDeleted, buildCourses, ids, parentIds);
        return courseController.searchCourseDTOS(parameterSearchCourse);
    }

    @PostMapping("/create")
    @Operation(description = "Tạo khoá học")
    public CourseDTO create(@RequestBody CourseDTO dto) {
        return courseController.createCourse(dto);
    }

    @PostMapping("/add-category/{course_id}")
    @Operation(description = "Liên kết khoá học với danh mục")
    public List<CategoryDTO> addCategory(@PathVariable("course_id") String cdId,
                                         @RequestParam(value = "category_ids", required = false) List<String> caIds) {
        return courseController.addCategoryToCourse(cdId, caIds);
    }

    @PostMapping("/accept/{course_id}")
    @Operation(description = "Xác nhận khoá học")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public void acceptCourse(@PathVariable("course_id") String courseId) {
        courseController.acceptCourse(courseId);
    }
}
