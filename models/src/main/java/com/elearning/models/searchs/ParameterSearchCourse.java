package com.elearning.models.searchs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterSearchCourse implements Serializable, Cloneable {
    private Integer level;

    private String searchType;
    
    private Boolean isDeleted;

    private String multiValue;

    private String createdBy;

    //true là build, rỗng hoặc false thì k build
    private Boolean buildChild;

    private String name;

    private String slug;

    private Date fromDate;

    private Date toDate;

    private BigDecimal priceFrom;

    private BigDecimal priceTo;

    private List<String> ids;

    private List<String> parentIds;

    private List<String> categoriesIds;

    //  page
    private Long startIndex;

    private Integer maxResult;
    @Override
    public ParameterSearchCourse clone() throws CloneNotSupportedException {
        return (ParameterSearchCourse) super.clone();
    }
}
