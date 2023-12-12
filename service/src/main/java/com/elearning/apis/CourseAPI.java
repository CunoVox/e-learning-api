package com.elearning.apis;

import com.elearning.annotation.validator.ValuesAllowed;
import com.elearning.controller.CourseController;
import com.elearning.models.dtos.CategoryDTO;
import com.elearning.models.dtos.CourseDTO;
import com.elearning.models.searchs.ParameterSearchCourse;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumCourseType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/course")
@Tag(name = "Course", description = "Course API")
@ExtensionMethod(Extensions.class)
public class CourseAPI {
    @Autowired
    private CourseController courseController;

    @GetMapping("/")
    @Operation(summary = "Danh sách khoá học")
    public ListWrapper<CourseDTO> getCourse(@RequestParam(value = "level", required = false) Integer level,
                                            @RequestParam(value = "multi_value", required = false) String multiValue,
                                            @RequestParam(value = "name", required = false) String name,
                                            @RequestParam(value = "slug", required = false) String slug,
                                            @RequestParam(value = "from_date", required = false) Long fromDate,
                                            @RequestParam(value = "to_date", required = false) Long toDate,
                                            @RequestParam(value = "price_from", required = false) BigDecimal priceFrom,
                                            @RequestParam(value = "price_to", required = false) BigDecimal priceTo,
                                            @RequestParam(value = "created_by", required = false) String createdBy,
                                            @RequestParam(value = "current_page", required = false) @Min(value = 1, message = "currentPage phải lớn hơn 0") @Parameter(description = "Default: 1") Integer currentPage,
                                            @RequestParam(value = "max_result", required = false) @Min(value = 1, message = "maxResult phải lớn hơn 0") @Max(value = 100, message = "maxResult phải bé hơn 101") @Parameter(description = "Default: 20; Size range: 1-100") Integer maxResult,
                                            @RequestParam(value = "search_type", required = false) EnumCourseType searchType,
                                            @RequestParam(value = "build_child", required = false) Boolean buildChild,
                                            @RequestParam(value = "is_deleted", required = false) Boolean isDeleted,
                                            @RequestParam(value = "ids", required = false) List<String> ids,
                                            @RequestParam(value = "sort_by", required = false) @ValuesAllowed(values = {"HIGHEST_RATING", "HIGHEST_SUB"}) @Parameter(description = "Allowed values: HIGHEST_RATING | HIGHEST_SUB") String sortBy,
                                            @RequestParam(value = "parent_ids", required = false) List<String> parentIds,
                                            @RequestParam(value = "categories_ids", required = false) List<String> categoriesIds) {
        if (currentPage == null || currentPage == 0) {
            currentPage = 1;
        }
        if (maxResult == null || maxResult == 0) {
            maxResult = 20;
        }
        Long startIndex = ((long) (currentPage - 1) * maxResult);
        ParameterSearchCourse parameterSearchCourse = new ParameterSearchCourse();
        parameterSearchCourse.setSlug(slug);
        parameterSearchCourse.setLevel(level);
        parameterSearchCourse.setMultiValue(multiValue);
        parameterSearchCourse.setName(name);
        parameterSearchCourse.setSlug(slug);
        parameterSearchCourse.setCreatedBy(createdBy);
        parameterSearchCourse.setIsDeleted(isDeleted);
        parameterSearchCourse.setIds(ids);
        parameterSearchCourse.setBuildChild(buildChild);
        parameterSearchCourse.setParentIds(parentIds);
        parameterSearchCourse.setStartIndex(startIndex);
        parameterSearchCourse.setCategoriesIds(categoriesIds);
        parameterSearchCourse.setSortBy(sortBy);
        if (fromDate != null) {
            parameterSearchCourse.setFromDate(new Date(fromDate));
        }
        if (toDate != null) {
            parameterSearchCourse.setToDate(new Date(toDate));
        }
        if (priceFrom != null) {
            parameterSearchCourse.setPriceFrom(priceFrom);
        }
        if (priceTo != null) {
            parameterSearchCourse.setPriceTo(priceTo);
        }
        if (searchType != null) {
            parameterSearchCourse.setSearchType(searchType.name());
        }
        parameterSearchCourse.setMaxResult(Objects.requireNonNullElse(maxResult, 20));

        return courseController.searchCourseDTOS(parameterSearchCourse);
    }

    @PostMapping("/create")
    @Operation(summary = "Tạo khoá học")
    @PreAuthorize("hasAnyRole('ROLE_LECTURE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public CourseDTO create(@RequestBody CourseDTO dto) {
        return courseController.createCourse(dto);
    }

    @PostMapping("/add-category/{course_id}")
    @Operation(summary = "Liên kết khoá học với danh mục")
    @PreAuthorize("hasAnyRole('ROLE_LECTURE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public List<CategoryDTO> addCategory(@PathVariable("course_id") String cdId,
                                         @RequestParam(value = "category_ids") List<String> caIds) {
        return courseController.addCategoryToCourse(cdId, caIds);
    }

    @PutMapping("/change-course-type")
    @Operation(summary = "Thay đổi tình trạng khoá học")
    @PreAuthorize("hasAnyRole('ROLE_LECTURE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public void changeCourseType(@RequestParam("course_id") String courseId,
                                 @RequestParam(value = "course_type") EnumCourseType course_type) {
        courseController.changeCourseType(courseId, course_type);
    }

    @PutMapping("/lecturer/change-course-type")
    @Operation(summary = "Thay đổi tình trạng khoá học từ DRAFT -> WAITING")
    @PreAuthorize("hasAnyRole('ROLE_LECTURE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public void changeCourseToWaiting(@RequestParam("course_id") String courseId) {
        courseController.changeCourseType(courseId, EnumCourseType.WAITING);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Ẩn hoặc bỏ ẩn khoá học")
    @PreAuthorize("hasAnyRole('ROLE_LECTURE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public void deleteCourse(@PathVariable("id") String courseId,
                             @RequestParam("is_deleted") Boolean isDeleted) {
        courseController.updateIsDeleted(courseId, isDeleted);
    }
}
