package com.elearning.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "enrollment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Enrollment extends IBaseEntity{

    private String courseId;
    private String userId;
    private String pricePurchase;
    private String currentCourse;
    private Long currentMillis;
    private int percentComplete;
    private List<String> completedCourse;
}
