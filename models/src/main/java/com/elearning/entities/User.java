package com.elearning.entities;

import com.elearning.utils.EnumRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "user")
public class User extends IBaseEntity{
    public String fullName;
    public String password;
    public String email;
    public String address;
    public List<EnumRole> roles = new ArrayList<>();
}
