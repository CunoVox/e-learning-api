package com.elearning.apis;

import com.elearning.controller.InvoiceController;
import com.elearning.models.dtos.InvoiceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

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
}
