package com.elearning.utils.enumAttribute;

import lombok.Getter;

public enum EnumLogActionType {
    CREATE("Tạo mới", 1),
    UPDATE("Cập nhật", 10),
    DELETE("Xóa", -1);

    @Getter
    String description;
    @Getter
    int value;

    EnumLogActionType(String description, int value) {
    }
}
