package com.elearning.entities;

import com.elearning.utils.enumAttribute.EnumCourseContentType;
import com.elearning.utils.enumAttribute.EnumCourseStatus;
import com.elearning.utils.enumAttribute.EnumCourseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Course extends IBaseEntity{
    private String partnerId;
    private EnumCourseType courseType;
    private EnumCourseStatus status;
    private String name;
    private String nameMode;
    private String slug;
    private String contentType;
    private String parentId;
    private int level;
    private String description;
    private String requirement;
    private Long duration;
    private Long totalLesson;
    private Long subscriptions;
    private Boolean isPublished;
}
