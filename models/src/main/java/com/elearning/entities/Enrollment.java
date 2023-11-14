package com.elearning.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "enrollment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Enrollment extends IBaseEntity{
    private String id;
    private String courseId;
    private String userId;
    private String pricePurchase;
    private String currentCourse;
    private Long currentMillis;
    private int percentComplete;
}
