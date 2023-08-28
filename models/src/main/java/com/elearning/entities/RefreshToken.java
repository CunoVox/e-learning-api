package com.elearning.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "refresh-token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RefreshToken extends IBaseEntity{
    public String createdFrom;
}
