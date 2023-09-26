package com.elearning.models.dtos;

import com.elearning.entities.CourseDraft;
import com.elearning.utils.enumAttribute.EnumCourseContentType;
import com.elearning.utils.enumAttribute.EnumCourseLevel;
import com.elearning.utils.enumAttribute.EnumCourseStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDraftDTO {
    private String id;
    private String name;
    @JsonProperty("name_mode")
    private String nameMode;
    private String slug;
    private EnumCourseContentType type;
    @JsonProperty("parent_id")
    private String parentId;
    private int level;
    private String description;
    private String requirement;
    private Long duration;
    @JsonProperty("total_lesson")
    private Long totalLesson;
    private Long subscriptions;
    private EnumCourseStatus status;
    private List<CourseDraftDTO> children;
    private List<CategoryDTO> categories;
    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("created_at")
    private Date createAt;

    @JsonProperty("update_by")
    private String updateBy;

    @JsonProperty("update_at")
    private Date updatedAt;

    @JsonProperty("is_deleted")
    private boolean isDeleted;
}
