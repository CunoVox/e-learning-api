package com.elearning.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Enrollment extends IBaseEntity{
    private String courseId;
    private String userId;
    private String currentCourse;
    private Long currentMillis;
    private int percentComplete;
}
