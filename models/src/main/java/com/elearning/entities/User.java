package com.elearning.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends IBaseEntity{
    public String fullName;
    public String password;
    public String email;
    public String address;

    public List<RefreshToken> refreshTokens;
}
