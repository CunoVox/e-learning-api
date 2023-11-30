package com.elearning.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {
    private String id;
    private String courseId;
    private String customerId;
    private String pricePurchase;
    private String status;
    private Date createdAt;
    private String createdBy;
    private Boolean isDeleted;
}
