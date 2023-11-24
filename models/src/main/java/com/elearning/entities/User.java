package com.elearning.entities;

import com.elearning.utils.enumAttribute.EnumRole;
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
public class User extends IBaseEntity {
    private String fullName;
    private String fullNameMod;
    private String password;
    private String email;
    public String address;
    public Boolean isEmailConfirmed = false;


    //lecturer
    private String phoneNumber;
    private String profileLink;


    public List<EnumRole> roles = new ArrayList<>();
}
