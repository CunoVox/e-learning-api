package com.elearning.models.dtos;

import com.elearning.utils.enumAttribute.EnumRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String id;
    @JsonProperty("full_name")
    private String fullName;
    private String email;
    private String address;
    @JsonIgnore
    private String password;
    private String avatar;
    @JsonProperty("is_deleted")
    private Boolean isDeleted;
    @JsonProperty("is_email_confirmed")
    private Boolean isEmailConfirmed;
    //lecturer
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("profile_link")
    private String profileLink;
    @JsonProperty("specialization")
    private String specialization;
    @JsonProperty("description")
    private String description;
    public List<EnumRole> roles = new ArrayList<>();
    @JsonProperty("total_course")
    public int totalCourse;
    @JsonProperty("total_subscriptions")
    public Long totalSubscriptions;
    @JsonProperty("average_rating")
    public double averageRating;
}
