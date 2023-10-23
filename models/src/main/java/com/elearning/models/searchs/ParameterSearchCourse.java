package com.elearning.models.searchs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterSearchCourse implements Serializable, Cloneable {
    private Integer level;

    private String searchType;
    
    private Boolean isDeleted;

    private Boolean buildCourses;

    private String buildType;

    private List<String> ids;
    private List<String> courseIds;

    private List<String> parentIds;
    @Override
    public ParameterSearchCourse clone() throws CloneNotSupportedException {
        return (ParameterSearchCourse) super.clone();
    }
}
