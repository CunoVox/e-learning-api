package com.elearning.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    public String id;
    public String fullName;
    @JsonIgnore
    public String password;
    public String email;
    public String address;
}
