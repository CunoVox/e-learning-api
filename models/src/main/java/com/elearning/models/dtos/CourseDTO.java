package com.elearning.models.dtos;

import com.elearning.utils.enumAttribute.EnumCourseType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {
    private String id;

    private String name;

    @JsonProperty("name_mode")
    private String nameMode;

    private String slug;

    private String type;

    private EnumCourseType courseType;

    @JsonProperty("parent_id")
    private String parentId;

    private int level;

    private String description;

    private String requirement;

    private Long duration;

    @JsonProperty("video_path")
    private String videoPath;

    @JsonProperty("image_path")
    private String imagePath;

    @JsonProperty("price_sell")
    private BigDecimal priceSell;

    @JsonProperty("price_promotion")
    private PriceDTO pricePromotion;

    @JsonProperty("total_lesson")
    private Long totalLesson;

    private Long subscriptions;

    private List<CourseDTO> children;

    @JsonProperty("category_ids")
    private List<String> categoryIds;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("created_user_info")
    private Map<String, String> createdUserInfo;

    private List<AttributeDTO> attributes;

    @JsonProperty("is_preview")
    private Boolean isPreview;

    @JsonProperty("created_at")
    private Date createAt;

    @JsonProperty("course_ratings")
    private CourseRatingDTO courseRatings;
    @JsonProperty("update_by")
    private String updateBy;

    private List<FileRelationshipDTO> attachments;

    @JsonProperty("update_at")
    private Date updatedAt;

    @JsonProperty("is_deleted")
    private boolean isDeleted;
}
