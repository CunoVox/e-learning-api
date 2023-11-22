package com.elearning.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
