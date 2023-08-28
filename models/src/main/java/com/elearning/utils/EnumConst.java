package com.elearning.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class EnumConst {

    public enum ProductOptionGroupSupTypeEnum {
        PRIVATE,
        PUBLIC
    }

    @AllArgsConstructor
    public enum ProductSubTypeEnum {
        TEMPLATE("Mẫu"),
        DRAFT("Nháp"),
        SIMPLE("Đơn giản"),
        SIMPLE_ARISE("Đơn giản tự tạo"),
        VARIABLE("Biến thể"),
        VARIABLE_ARISE("Biến thể tự tạo"),
        OPTION("Tùy chọn"),
        SET_ARISE("SET tự tạo"),
        COMBO_ARISE("COMBO tự tạo"),
        COMBO("Combo");
        @Getter
        String description;
    }
}
