package com.elearning.entities;

import com.elearning.utils.enumAttribute.EnumCommentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Comment extends IBaseEntity{
    private String userId;
    private String referenceId;

    private String content;
    private String parentId;
    private int level;

    private EnumCommentType type;
}
