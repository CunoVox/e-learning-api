package com.elearning.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class UserDTO {
    public String id;
    public String fullName;
    @JsonIgnore
    public String password;
    public String email;
    public String address;
}
