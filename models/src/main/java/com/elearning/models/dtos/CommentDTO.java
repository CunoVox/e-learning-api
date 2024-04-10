package com.elearning.models.dtos;

import com.elearning.utils.enumAttribute.EnumCommentType;
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
public class CommentDTO {
    private String id;
    private String content;
    private String userId;

    private int level;
    private EnumCommentType type;
    @JsonProperty("reference_id")
    private String referenceId;

    @JsonProperty("parent_id")
    private String parentId;

    private List<CommentDTO> reply;

    @JsonProperty("created_at")
    private Date createAt;

    @JsonProperty("create_by")
    private String createBy;

    @JsonProperty("update_by")
    private String updateBy;

    @JsonProperty("update_at")
    private Date updatedAt;

    @JsonProperty("is_deleted")
    private boolean isDeleted;

    @JsonProperty("user_detail")
    private UserProfileDTO userDetail;

}
