package com.elearning.utils.enumAttribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumCategoryBuildType {
    TREE("Dạng cây"),
    LIST("Dạng list");

    final String description;
}
