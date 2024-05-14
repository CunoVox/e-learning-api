package com.elearning.models.searchs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParameterSearchInvoice {
    private Date fromDate;
    private Date toDate;
    private Integer currentPage;
    private Integer maxResult;
    private String sellerId;
    private String userId;
}
