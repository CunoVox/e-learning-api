package com.elearning.utils.enumAttribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
public enum EnumRelatedObjectsStatus {
    ACTIVE(1),
    INACTIVE(0);

    @Getter
    int value;
//
//    EnumRelatedObjectsStatus(int value) {
//    }
}
