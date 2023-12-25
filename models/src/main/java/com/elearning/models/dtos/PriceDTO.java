package com.elearning.models.dtos;

import com.elearning.utils.enumAttribute.EnumPriceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceDTO {
    private String id;
    private EnumPriceType type;
    private BigDecimal price;
    private String parentId;
    private Date fromDate;
    private Date toDate;
}
