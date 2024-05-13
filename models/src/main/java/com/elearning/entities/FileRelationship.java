package com.elearning.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "file_relationship")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FileRelationship extends IBaseEntity{
    private String parentId;
    private String parentType;
    private String fileId;
    private String mimeType;
    private String name;
    private Long size;
    private Long duration;
    private String webViewLink;
    private String pathFile;
    private String downloadLink;
}
