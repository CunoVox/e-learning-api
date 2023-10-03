package com.elearning.utils.enumAttribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumCourseLevel {
    LEVEL_1("Khóa học"),
    LEVEL_2("Chương"),
    LEVEL_3("Bài học");
    final String description;
}
