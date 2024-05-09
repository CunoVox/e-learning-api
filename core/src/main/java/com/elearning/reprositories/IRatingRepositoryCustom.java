package com.elearning.reprositories;

import java.util.List;
import java.util.Map;

public interface IRatingRepositoryCustom {
    Map<String, Double> avgRattingByCourseCreatedByIn(List<String> createdBy);
}
