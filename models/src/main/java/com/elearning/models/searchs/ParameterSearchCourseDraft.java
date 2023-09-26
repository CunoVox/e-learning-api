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
public class ParameterSearchCourseDraft implements Serializable, Cloneable {
    private Integer level;

    private Boolean isDeleted;

    private Boolean buildCourses;

    private String buildType;

    private List<String> categoriesIds;

    private List<String> parentIds;
    @Override
    public ParameterSearchCourseDraft clone() throws CloneNotSupportedException {
        return (ParameterSearchCourseDraft) super.clone();
    }
}
