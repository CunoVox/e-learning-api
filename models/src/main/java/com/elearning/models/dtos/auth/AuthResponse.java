package com.elearning.models.dtos.auth;

import com.elearning.models.dtos.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private String refreshToken;
    private UserDTO user;

}
