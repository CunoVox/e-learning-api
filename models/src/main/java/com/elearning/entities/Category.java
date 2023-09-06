package com.elearning.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Category extends IBaseEntity{
    private String name;
    private String parentId;
    private String createdBy;
}
