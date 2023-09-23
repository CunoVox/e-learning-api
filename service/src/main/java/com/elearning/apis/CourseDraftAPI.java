package com.elearning.apis;

import com.elearning.controller.CourseDraftController;
import com.elearning.models.dtos.CourseDraftDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("api/course-draft")
public class CourseDraftAPI {
    @Autowired
    private CourseDraftController courseDraftController;

    @PostMapping
    public CourseDraftDTO create(@RequestBody CourseDraftDTO dto){
        return courseDraftController.createCourseDraft(dto);
    }
}
