package com.elearning.entities;

import com.elearning.utils.enumAttribute.EnumPriceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Document(collection = "price")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Price extends IBaseEntity{
    private EnumPriceType type;
    private BigDecimal price;
    private String parentId;
    private Date fromDate;
    private Date toDate;
}
