package com.elearning.apis;

import com.elearning.controller.EnrollmentController;
import com.elearning.models.dtos.EnrollmentDTO;
import com.elearning.models.searchs.ParameterSearchEnrollment;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.utils.enumAttribute.EnumCourseType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentAPI {
    @Autowired
    EnrollmentController enrollmentController;

    @PostMapping("/")
    public EnrollmentDTO courseEnroll(@RequestBody EnrollmentDTO dto) {
        return enrollmentController.createEnrollment(dto);
    }

    @GetMapping("/check-enrollment/{course_id}")
    public Boolean checkEnrollment(@PathVariable("course_id") String courseId) {
        return enrollmentController.checkEnrollment(courseId);
    }

    @GetMapping("/my-enrollment")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_LECTURE', 'ROLE_USER')")
    @Operation(summary = "Danh sách đã enroll của user đang đăng nhập")
    public ListWrapper<EnrollmentDTO> myEnrollments(@RequestParam(value = "ids", required = false) List<String> ids,
                                                    @RequestParam(value = "course_ids", required = false) List<String> courseIds,
                                                    @RequestParam(value = "percent_complete", required = false) @Min(value = 0, message = "percentComplete phải lớn hơn 0") @Parameter(description = "Default: 0") Integer percentComplete,
                                                    @RequestParam(value = "currentPage", required = false) @Min(value = 1, message = "currentPage phải lớn hơn 0") @Parameter(description = "Default: 1") Integer currentPage,
                                                    @RequestParam(value = "maxResult", required = false) @Min(value = 1, message = "maxResult phải lớn hơn 0") @Max(value = 100, message = "maxResult phải bé hơn 101") @Parameter(description = "Default: 20; Size range: 1-100") Integer maxResult,
                                                    @RequestParam(value = "is_deleted", required = false) Boolean isDeleted) {
        ParameterSearchEnrollment parameterSearchEnrollment = setParameterSearchEnrollment(ids, courseIds, percentComplete, currentPage, maxResult, isDeleted);
        return enrollmentController.userEnrollments(parameterSearchEnrollment);
    }

    @GetMapping("/user-enrollment")
    @Operation(summary = "Danh sách đã enroll")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_LECTURE')")
    public ListWrapper<EnrollmentDTO> userEnrollments(@RequestParam(value = "ids", required = false) List<String> ids,
                                                      @RequestParam(value = "course_ids", required = false) List<String> courseIds,
                                                      @RequestParam(value = "user_ids", required = false) List<String> userIds,
                                                      @RequestParam(value = "percent_complete", required = false) @Min(value = 0, message = "percentComplete phải lớn hơn 0") @Parameter(description = "Default: 0") Integer percentComplete,
                                                      @RequestParam(value = "currentPage", required = false) @Min(value = 1, message = "currentPage phải lớn hơn 0") @Parameter(description = "Default: 1") Integer currentPage,
                                                      @RequestParam(value = "maxResult", required = false) @Min(value = 1, message = "maxResult phải lớn hơn 0") @Max(value = 100, message = "maxResult phải bé hơn 101") @Parameter(description = "Default: 20; Size range: 1-100") Integer maxResult,
                                                      @RequestParam(value = "is_deleted", required = false) Boolean isDeleted) {

        ParameterSearchEnrollment parameterSearchEnrollment = setParameterSearchEnrollment(ids, courseIds, percentComplete, currentPage, maxResult, isDeleted);
        parameterSearchEnrollment.setUserIds(userIds);
        return enrollmentController.searchEnrollments(parameterSearchEnrollment);
    }


    private ParameterSearchEnrollment setParameterSearchEnrollment(@RequestParam(value = "ids", required = false) List<String> ids,
                                                                   @RequestParam(value = "course_ids", required = false) List<String> courseIds,
                                                                   @Parameter(description = "Default: 0") @RequestParam(value = "percent_complete", required = false) @Min(value = 0, message = "percentComplete phải lớn hơn 0") Integer percentComplete,
                                                                   @Parameter(description = "Default: 1") @RequestParam(value = "currentPage", required = false) @Min(value = 1, message = "currentPage phải lớn hơn 0") Integer currentPage,
                                                                   @Parameter(description = "Default: 20; Size range: 1-100") @RequestParam(value = "maxResult", required = false) @Min(value = 1, message = "maxResult phải lớn hơn 0") @Max(value = 100, message = "maxResult phải bé hơn 101") Integer maxResult,
                                                                   @RequestParam(value = "is_deleted", required = false) Boolean isDeleted) {
        if (currentPage == null || currentPage == 0) {
            currentPage = 1;
        }
        if (maxResult == null || maxResult == 0) {
            maxResult = 20;
        }
        Long startIndex = ((long) (currentPage - 1) * maxResult);

        ParameterSearchEnrollment parameterSearchEnrollment = new ParameterSearchEnrollment();
        parameterSearchEnrollment.setIds(ids);
        parameterSearchEnrollment.setCourseIds(courseIds);
        if (percentComplete != null) {
            parameterSearchEnrollment.setPercentComplete(percentComplete);
        }
        parameterSearchEnrollment.setIsDeleted(isDeleted);
        parameterSearchEnrollment.setMaxResult(maxResult);
        parameterSearchEnrollment.setStartIndex(startIndex);

        return parameterSearchEnrollment;
    }
}
