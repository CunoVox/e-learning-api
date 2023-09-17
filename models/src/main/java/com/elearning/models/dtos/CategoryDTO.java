package com.elearning.models.dtos;

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
public class CategoryDTO {
    private String id;

    private String title;

    private String image;

    @JsonProperty("parent_id")
    private String parentId;

    private int level;

    private List<CategoryDTO> childs;

    private List<CourseDTO> courses;

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
