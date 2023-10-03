package com.elearning.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Category extends IBaseEntity implements Serializable {
    private String name;
    private String nameMode;
    private String slug;
    private int level;
    private String parentId;
    private String createdBy;
    private String updateBy;
}
