package com.elearning.utils.enumAttribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EnumPriceType {
    SELL("Giá bán mặc định"),
    PROMOTION("Giá bán khuyến mãi");

    @Getter
    String description;
}
