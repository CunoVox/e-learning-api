package com.elearning.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Tag {
    private String name;
}
