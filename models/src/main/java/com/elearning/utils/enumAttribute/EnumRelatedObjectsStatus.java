package com.elearning.utils.enumAttribute;

import lombok.Getter;

public enum EnumRelatedObjectsStatus {
    ACTIVE(1),
    INACTIVE(0);

    @Getter
    int value;

    EnumRelatedObjectsStatus(int value) {
    }
}
