package com.elearning.controller;

import com.elearning.config.VnpayConfig;
import com.elearning.entities.Category;
import com.elearning.entities.Invoice;
import com.elearning.entities.User;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.CourseDTO;
import com.elearning.models.dtos.InvoiceDTO;
import com.elearning.reprositories.ICourseRepository;
import com.elearning.reprositories.IInvoiceRepository;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.reprositories.IUserRepository;
import com.elearning.utils.Constants;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumCourseType;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
@Component
@ExtensionMethod(Extensions.class)
public class InvoiceController extends BaseController{
    @Autowired
    private ISequenceValueItemRepository sequenceValueItemRepository;
    @Autowired
    private ICourseRepository courseRepository;
    @Autowired
    private IInvoiceRepository invoiceRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private CourseController courseController;

    public String getUrlPayment(String courseId, String customerId) throws UnsupportedEncodingException {
        validateInvoice(courseId, customerId);
        CourseDTO courseDTO = courseController.getCourseById(courseId);
        String vnp_TxnRef = VnpayConfig.getRandomNumber(8);

        Map<String, String> vnp_Params = new HashMap<>();

        vnp_Params.put("vnp_Version", Constants.VNP_VERSION);
        vnp_Params.put("vnp_Command", Constants.VNP_COMMAND);
        vnp_Params.put("vnp_TmnCode", Constants.VNP_TMN_CODE);
        vnp_Params.put("vnp_Amount", String.valueOf(courseDTO.getPriceSell().intValue() * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", Constants.VNP_RETURN_URL);
        vnp_Params.put("vnp_IpAddr", "13.160.92.202");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnpayConfig.hmacSHA512(Constants.VNP_SECRET_KEY, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return Constants.VNP_PAY_URL + "?" + queryUrl;
    }

    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        validateInvoice(invoiceDTO.getCourseId(), invoiceDTO.getCustomerId());
        invoiceDTO.setCreatedBy(getUserIdFromContext());
        return toDTO(invoiceRepository.save(toEntity(invoiceDTO)));
    }

    public InvoiceDTO getInvoice(String courseId, String customerId) {
        return  toDTO(invoiceRepository.findByCourseIdAndCustomerId(courseId, customerId));
    }

    private void validateInvoice(String courseId, String customerId) {
        CourseDTO course = courseController.getCourseById(courseId);
        if (course == null) {
            throw new ServiceException("Khoá học không tồn tại trong hệ thống");
        }
        if (!course.getCourseType().equals(EnumCourseType.CHANGE_PRICE) && !course.getCourseType().equals(EnumCourseType.OFFICIAL)) {
            throw new ServiceException("Khoá học không đủ điều kiện");
        }
        if (course.getPriceSell().compareTo(BigDecimal.valueOf(0))<1) {
            throw new ServiceException("Khoá học miễn phí không thể thực hiện thao tác");
        }
        Optional<User> user = userRepository.findById(customerId);
        if (user.isEmpty()){
            throw new ServiceException("Không tìm thấy người dùng trong hệ thống");
        }
        if (user.get().getIsDeleted()!=null && user.get().getIsDeleted()) {
            throw new ServiceException("Người dùng đã bị khoá");
        }
    }

    public Invoice toEntity(InvoiceDTO dto) {
        if (dto == null) return new Invoice();
        return Invoice.builder()
                .id(sequenceValueItemRepository.getSequence(Category.class))
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getCreatedBy())
                .courseId(dto.getCourseId())
                .customerId(dto.getCustomerId())
                .pricePurchase(dto.getPricePurchase())
                .status(dto.getStatus())
                .build();
    }

    public InvoiceDTO toDTO(Invoice dto) {
        if (dto == null) return new InvoiceDTO();
        return InvoiceDTO.builder()
                .id(dto.getId())
                .createdBy(dto.getCreatedBy())
                .createdAt(dto.getCreatedAt())
                .courseId(dto.getCourseId())
                .customerId(dto.getCustomerId())
                .pricePurchase(dto.getPricePurchase())
                .status(dto.getStatus())
                .build();
    }
}
