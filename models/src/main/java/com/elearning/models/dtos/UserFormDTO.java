package com.elearning.models.dtos;

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
public class UserFormDTO {
    @NotNull(message = "Email chưa được nhập")
    @NotBlank(message = "Email chưa được nhập")
    public String email;
    public String fullName;
    public String address;
    @NotNull(message = "Mật khẩu chưa được nhập")
    @NotBlank(message = "Mật khẩu chưa được nhập")
    public String password;

}
