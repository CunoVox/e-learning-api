package com.elearning.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserDTO {
    public String id;
    public String fullName;
    @JsonIgnore
    public String password;
    public String email;
    public String address;
}
