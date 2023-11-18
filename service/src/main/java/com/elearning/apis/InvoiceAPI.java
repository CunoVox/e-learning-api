package com.elearning.apis;

import com.elearning.controller.InvoiceController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
@Tag(name = "Invoice", description = "Invoice API")
public class InvoiceAPI {
    @Autowired
    private InvoiceController invoiceController;

    @Operation(summary = "Lấy url thanh toán")
    @PostMapping(path = "/url")
    public String getUrlPayment(@RequestParam(value = "amount") BigDecimal amount) throws UnsupportedEncodingException {
        return invoiceController.GetUrlPayment(amount);
    }
}
