package com.elearning.models.searchs;

import com.elearning.utils.enumAttribute.EnumUserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterSearchUser {
    Date fromDate;
    Date toDate;
    String multiValue;
    List<String> userIds;
    EnumUserStatus status;
    private Long startIndex;
    private Integer maxResult;
}
