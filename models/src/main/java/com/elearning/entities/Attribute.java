package com.elearning.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Attribute extends IBaseEntity{
//    private String forId;
    private String attributeName;
    private Object attributeValue;
//    private String forCollection;
}
