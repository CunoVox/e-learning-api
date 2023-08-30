package com.elearning.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    public String id;
    public String fullName;
    @JsonIgnore
    public String password;
    public String email;
    public String address;
    public Boolean isDeleted;

}
