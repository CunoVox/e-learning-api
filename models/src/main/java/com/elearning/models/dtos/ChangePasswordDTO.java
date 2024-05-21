package com.elearning.models.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {
    @NotNull(message = "Mật khẩu chưa được nhập")
    @NotBlank(message = "Mật khẩu chưa được nhập")
    private String currentPassword;
    @NotNull(message = "Mật khẩu chưa được nhập")
    @NotBlank(message = "Mật khẩu chưa được nhập")
    private String newPassword;
    @NotNull(message = "Mật khẩu chưa được nhập")
    @NotBlank(message = "Mật khẩu chưa được nhập")
    private String confirmPassword;
}
