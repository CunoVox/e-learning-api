package com.elearning.entities;


import com.elearning.utils.enumAttribute.EnumCourseLevel;
import com.elearning.utils.enumAttribute.EnumCourseStatus;
import com.elearning.utils.enumAttribute.EnumCourseContentType;
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
public class Course extends CourseDraft{
//    private String name;
    private String courseDraftId;
//    private String nameMode;
//    private String slug;
//    private EnumCourseContentType type;
//    private String parentId;
//    private EnumCourseLevel level;
//    private String description;
//    private String requirement;
//    private Long duration;
//    private Long totalLesson;
//    private Long subscriptions;
//    private String createdBy;
//    private EnumCourseStatus status;
}
