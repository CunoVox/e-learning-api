package com.elearning.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserFormDTO {
    @NotNull
    public String email;
    public String fullName;
    public String address;
    @NotNull
    public String password;

}
