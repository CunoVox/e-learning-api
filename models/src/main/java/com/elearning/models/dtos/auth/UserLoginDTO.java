package com.elearning.models.dtos.auth;

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
public class UserLoginDTO {
    @NotNull(message = "Email chưa được nhập")
    @NotBlank(message = "Email chưa được nhập")
    private String email;

    @NotNull(message = "Mật khẩu chưa được nhập")
    @NotBlank(message = "Mật khẩu chưa được nhập")
    private String password;

}
