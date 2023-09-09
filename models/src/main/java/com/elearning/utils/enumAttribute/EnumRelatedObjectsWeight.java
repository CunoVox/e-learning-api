package com.elearning.utils.enumAttribute;

import lombok.Getter;

public enum EnumRelatedObjectsWeight {
    LOW(0),
    MEDIUM(1),
    HIGH(2);

    @Getter
    int value;

    EnumRelatedObjectsWeight(int value) {
    }
}
