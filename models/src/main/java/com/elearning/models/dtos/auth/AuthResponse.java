package com.elearning.models.dtos.auth;

import com.elearning.models.dtos.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    //    @JsonIgnore
    @JsonProperty("refresh_token")
    private String refreshToken;
    private UserDTO user;

}
