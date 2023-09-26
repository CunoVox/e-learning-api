package com.elearning.apis;

import com.elearning.controller.CourseDraftController;
import com.elearning.models.dtos.CourseDraftDTO;
import com.elearning.models.searchs.ParameterSearchCourseDraft;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumListBuildType;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-draft")
@ExtensionMethod(Extensions.class)
public class CourseDraftAPI {
    @Autowired
    private CourseDraftController courseDraftController;
    private ParameterSearchCourseDraft buildParameterSearch(
            @RequestParam(value = "build_type") EnumListBuildType buildType,
            @RequestParam(value = "level", required = false) Integer level,
            @RequestParam(value = "is_deleted", required = false) Boolean isDeleted,
            @RequestParam(value = "build_courses", required = false) Boolean buildCourses,
            @RequestParam(value = "categories_ids", required = false) List<String> categoriesIds,
            @RequestParam(value = "parent_ids", required = false) List<String> parentIds){

        ParameterSearchCourseDraft parameterSearchCourseDraft = new ParameterSearchCourseDraft();

        if (buildType != null) {
            parameterSearchCourseDraft.setBuildType(buildType.name());
        }
        if (level != null) {
            parameterSearchCourseDraft.setLevel(level);
        }
        if (isDeleted != null) {
            parameterSearchCourseDraft.setIsDeleted(isDeleted);
        }
        if (buildCourses != null) {
            parameterSearchCourseDraft.setBuildCourses(buildCourses);
        }
        if (!categoriesIds.isNullOrEmpty()) {
            parameterSearchCourseDraft.setCategoriesIds(categoriesIds);
        }
        if (!parentIds.isNullOrEmpty()) {
            parameterSearchCourseDraft.setParentIds(parentIds);
        }
        return parameterSearchCourseDraft;
    }
    @GetMapping("/")
    public List<CourseDraftDTO> getCourseDraft(@RequestParam(value = "build_type") EnumListBuildType buildType,
                                           @RequestParam(value = "level", required = false) Integer level,
                                           @RequestParam(value = "is_deleted", required = false) Boolean isDeleted,
                                           @RequestParam(value = "build_courses", required = false) Boolean buildCourses,
                                           @RequestParam(value = "categories_ids", required = false) List<String> categoriesIds,
                                           @RequestParam(value = "parent_ids", required = false) List<String> parentIds) {
        ParameterSearchCourseDraft parameterSearchCourseDraft =
                this.buildParameterSearch(buildType, level, isDeleted, buildCourses, categoriesIds, parentIds);
        return courseDraftController.searchCourseDraftDTOS(parameterSearchCourseDraft);
    }

    @PostMapping("/create")
    public CourseDraftDTO create(@RequestBody CourseDraftDTO dto){
        return courseDraftController.createCourseDraft(dto);
    }
}
