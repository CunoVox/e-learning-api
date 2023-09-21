package com.elearning.entities;

import com.elearning.utils.enumAttribute.EnumCourseContentType;
import com.elearning.utils.enumAttribute.EnumCourseLevel;
import com.elearning.utils.enumAttribute.EnumCourseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "course_draft")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CourseDraft extends IBaseEntity{
    private String name;
    private String nameMode;
    private String slug;
    private EnumCourseContentType type;
    private String parentId;
    private int level;
    private String description;
    private String requirement;
    private Long duration;
    private Long totalLesson;
    private Long subscriptions;
    private String createdBy;

    private Boolean isPublished;
    private EnumCourseStatus status;

}
