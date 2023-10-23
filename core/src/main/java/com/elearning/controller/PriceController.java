package com.elearning.controller;

import com.elearning.entities.Course;
import com.elearning.entities.Price;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.PriceDTO;
import com.elearning.reprositories.ICourseRepository;
import com.elearning.reprositories.IPriceRepository;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumPriceType;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
@ExtensionMethod(Extensions.class)
public class PriceController extends BaseController {
    @Autowired
    IPriceRepository priceRepository;

    @Autowired
    ICourseRepository courseRepository;

    public PriceDTO createPrice(PriceDTO priceDTO) {
        Optional<Course> course = courseRepository.findById(priceDTO.getParentId());
        if (course.isEmpty()) throw new ServiceException("Khoá học không tồn tại");
        Price price = toEntity(priceDTO);
        price.setCreatedBy(getUserIdFromContext());
        return toDTO(priceRepository.save(price));
    }

    public PriceDTO updatePrice(PriceDTO priceDTO) {
        Optional<Price> price = priceRepository.findById(priceDTO.getId());
        if (price.isEmpty()) throw new ServiceException("Cập nhật giá tiền khoá học không thành công!");
        Optional<Course> course = courseRepository.findById(priceDTO.getParentId());
        if (course.isEmpty()) throw new ServiceException("Khoá học không tồn tại");
        else {
            price.get().setPrice(priceDTO.getPrice());
            if (priceDTO.getFromDate() != null)
                price.get().setFromDate(priceDTO.getFromDate());
            if (priceDTO.getToDate() != null)
                price.get().setToDate(priceDTO.getToDate());
            price.get().setUpdatedAt(new Date());
            price.get().setUpdatedBy(getUserIdFromContext());
            return toDTO(priceRepository.save(price.get()));
        }
    }

    public PriceDTO updatePriceSell(String parentId, BigDecimal price){
        Price priceSell = priceRepository.findByParentIdAndType(parentId, EnumPriceType.SELL.name());
        if (priceSell !=null){
            priceSell.setPrice(price);
            return this.updatePrice(toDTO(priceSell));
        } else {
            return createPrice(PriceDTO.builder()
                    .price(price)
                    .parentId(parentId)
                    .type(EnumPriceType.SELL)
                    .build());
        }
    }

    public Price toEntity(PriceDTO priceDTO) {
        return Price.builder()
                .type(priceDTO.getType())
                .price(priceDTO.getPrice())
                .parentId(priceDTO.getParentId())
                .fromDate(priceDTO.getFromDate())
                .toDate(priceDTO.getToDate())
                .build();
    }

    public PriceDTO toDTO(Price price) {
        return PriceDTO.builder()
                .id(price.getId())
                .type(price.getType())
                .price(price.getPrice())
                .parentId(price.getParentId())
                .fromDate(price.getFromDate())
                .toDate(price.getToDate())
                .build();
    }
}
