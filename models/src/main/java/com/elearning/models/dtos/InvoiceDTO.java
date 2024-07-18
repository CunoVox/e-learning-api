package com.elearning.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {
    private String id;
    @NotBlank(message = "Không được bỏ trống mã khoá học")
    private String courseId;
    @NotBlank(message = "Không được bỏ trống mã khách hàng")
    private String customerId;
    @NotBlank(message = "Không được bỏ trống giá tiền")
    private BigDecimal pricePurchase;
    private String status;
    //dùng để render ra view
    private String courseName;
    private String courseSlug;
    private String customerName;
    private String customerEmail;
    private String customerImage;
    private String sellerName;
    private String sellerEmail;
    private String sellerImage;
    //==================================================================
    private Date createdAt;
    private String createdBy;
    private Boolean isDeleted;
}
