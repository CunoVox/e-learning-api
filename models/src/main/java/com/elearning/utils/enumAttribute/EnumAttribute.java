package com.elearning.utils.enumAttribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public enum EnumAttribute {
    COURSE_SELL_PRICE("Giá tiền bán khoá học");

    String description;
}
