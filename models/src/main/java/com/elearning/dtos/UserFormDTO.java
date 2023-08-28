package com.elearning.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserFormDTO {
    @NotNull(message = "Email chưa được nhập")
    public String email;
    public String fullName;
    public String address;
    @NotNull(message = "Mật khẩu chưa được nhập")
    public String password;

}
