package com.elearning.models.searchs;

import com.elearning.utils.enumAttribute.EnumCommentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterSearchComment implements Serializable, Cloneable{

    private Integer level;

    private Boolean isDeleted;

//    private String buildType;

    private String ReferenceId;
    private EnumCommentType type;
    //  page
    private Long startIndex;

    private Integer maxResult;
//    private List<String> categoriesIds;
//
//    private List<String> parentIds;

    @Override
    public ParameterSearchComment clone() throws CloneNotSupportedException {
        return (ParameterSearchComment) super.clone();
    }
}
