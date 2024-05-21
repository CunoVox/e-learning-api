package com.elearning.apis;

import com.elearning.controller.InvoiceController;
import com.elearning.models.dtos.InvoiceDTO;
import com.elearning.models.searchs.ParameterSearchInvoice;
import com.elearning.models.wrapper.ListWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
@Tag(name = "Invoice", description = "Invoice API")
public class InvoiceAPI {
    @Autowired
    private InvoiceController invoiceController;

    @Operation(summary = "Lấy url thanh toán")
    @PostMapping(path = "/url/{course_id}/{customer_id}")
    public String getUrlPayment(@PathVariable(value = "course_id") String courseId,
                                @PathVariable(value = "customer_id") String customerId) throws UnsupportedEncodingException {
        return invoiceController.getUrlPayment(courseId, customerId);
    }

    @Operation(summary = "Tạo hoá đơn")
    @PostMapping(path = "/create")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_LECTURE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public InvoiceDTO createInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        return invoiceController.createInvoice(invoiceDTO);
    }

    @Operation(summary = "Danh sách hoá đơn")
    @GetMapping(path = "/")
    @PreAuthorize("hasAnyRole('ROLE_LECTURE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ListWrapper<InvoiceDTO> getInvoices(@RequestParam(value = "from_date", required = false) Long fromDate,
                                               @RequestParam(value = "to_date", required = false) Long toDate,
                                               @RequestParam(value = "current_page", required = false) Integer currentPage,
                                               @RequestParam(value = "max_result", required = false) Integer maxResult,
                                               @RequestParam(value = "user_id", required = false) String userId,
                                               @RequestParam(value = "is_current_user", required = false) Boolean isCurrentUser) {
        ParameterSearchInvoice parameterSearchInvoice = ParameterSearchInvoice.builder()
                .currentPage(currentPage)
                .maxResult(maxResult)
                .sellerId(isCurrentUser!=null && isCurrentUser ? invoiceController.getUserIdFromContext() : null)
                .build();
        if(userId != null) {
            parameterSearchInvoice.setUserId(userId);
        }
        if (fromDate != null) {
            parameterSearchInvoice.setFromDate(new Date(fromDate));
        }
        if (toDate != null) {
            parameterSearchInvoice.setToDate(new Date(toDate));
        }
        return invoiceController.searchInvoice(parameterSearchInvoice);
    }
}
