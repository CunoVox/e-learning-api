package com.elearning.apis;

import com.elearning.controller.EnrollmentController;
import com.elearning.models.dtos.EnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentAPI {
    @Autowired
    EnrollmentController enrollmentController;

    @PostMapping("/{course_id}")
    public EnrollmentDTO enrollCourse(@RequestBody EnrollmentDTO dto){
        return enrollmentController.createEnrollment(dto);
    }
}
