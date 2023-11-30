package com.elearning.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingDTO {
    private String id;
    @JsonProperty("course_id")
    private String courseId;
    @JsonProperty("user_id")
    private String userId;
    private int rate;

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
