package com.elearning.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String fullName;
    @JsonIgnore
    private String password;
    private String email;
    private String address;
    private Boolean isDeleted;
    private Boolean isEmailConfirmed;
}
