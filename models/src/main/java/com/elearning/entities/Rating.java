package com.elearning.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Rating extends IBaseEntity{
    private String courseId;
    private String userId;
    private int rate;
}
