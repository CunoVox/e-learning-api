package com.elearning.models.dtos.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterDTO {
    @NotNull(message = "Email chưa được nhập")
    @NotBlank(message = "Email chưa được nhập")
    private String email;
    @JsonProperty("full_name")
    private String fullName;
    private String address;
    @NotNull(message = "Mật khẩu chưa được nhập")
    @NotBlank(message = "Mật khẩu chưa được nhập")
    private String password;

}
