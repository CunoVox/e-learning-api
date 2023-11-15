package com.elearning.models.searchs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterSearchEnrollment implements Serializable, Cloneable{
    private List<String> ids;
    private List<String> courseIds;
    private List<String> userIds;
    private String currentCourse;
    private String pricePurchase;
    private int percentComplete;
    private Boolean isDeleted;

    //  page
    private Long startIndex;

    private Integer maxResult;
    @Override
    public ParameterSearchEnrollment clone() throws CloneNotSupportedException {
        return (ParameterSearchEnrollment) super.clone();
    }
}
