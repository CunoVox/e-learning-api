package com.elearning.utils.enumAttribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EnumCategoryBuildType {
    TREE("Dạng cây"),
    LIST("Dạng list");

    @Getter
    String description;
}
