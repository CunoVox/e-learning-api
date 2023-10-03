package com.elearning.apis;

import com.elearning.controller.CourseController;
import com.elearning.models.dtos.CategoryDTO;
import com.elearning.models.dtos.CourseDTO;
import com.elearning.models.searchs.ParameterSearchCourse;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumListBuildType;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
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
            @RequestParam(value = "level", required = false) Integer level,
            @RequestParam(value = "is_draft", required = false) Boolean isDraft,
            @RequestParam(value = "is_deleted", required = false) Boolean isDeleted,
            @RequestParam(value = "build_courses", required = false) Boolean buildCourses,
            @RequestParam(value = "categories_ids", required = false) List<String> categoriesIds,
            @RequestParam(value = "parent_ids", required = false) List<String> parentIds){

        ParameterSearchCourse parameterSearchCourse = new ParameterSearchCourse();

        if (buildType != null) {
            parameterSearchCourse.setBuildType(buildType.name());
        }
        if (level != null) {
            parameterSearchCourse.setLevel(level);
        }
        if(isDraft != null){
            parameterSearchCourse.setIsDraft(isDraft);
        }
        if (isDeleted != null) {
            parameterSearchCourse.setIsDeleted(isDeleted);
        }
        if (buildCourses != null) {
            parameterSearchCourse.setBuildCourses(buildCourses);
        }
        if (!categoriesIds.isNullOrEmpty()) {
            parameterSearchCourse.setCategoriesIds(categoriesIds);
        }
        if (!parentIds.isNullOrEmpty()) {
            parameterSearchCourse.setParentIds(parentIds);
        }
        return parameterSearchCourse;
    }
    @GetMapping("/")
    public List<CourseDTO> getCourse(@RequestParam(value = "build_type") EnumListBuildType buildType,
                                     @RequestParam(value = "level", required = false) Integer level,
                                     @RequestParam(value = "is_draft", required = false) Boolean isDraft,
                                     @RequestParam(value = "is_deleted", required = false) Boolean isDeleted,
                                     @RequestParam(value = "build_courses", required = false) Boolean buildCourses,
                                     @RequestParam(value = "categories_ids", required = false) List<String> categoriesIds,
                                     @RequestParam(value = "parent_ids", required = false) List<String> parentIds) {
        ParameterSearchCourse parameterSearchCourse =
                this.buildParameterSearch(buildType, level, isDraft, isDeleted, buildCourses, categoriesIds, parentIds);
        return courseController.searchCourseDTOS(parameterSearchCourse);
    }

    @PostMapping("/create")
    public CourseDTO create(@RequestBody CourseDTO dto){
        return courseController.createCourse(dto);
    }
    @PostMapping("/add-category/{course_id}")
    public List<CategoryDTO> addCategory(@PathVariable("course_id") String cdId,
                                   @RequestParam(value = "category_ids", required = false) List<String> caIds){
        return courseController.addCategoryToCourse(cdId, caIds);
    }

}
