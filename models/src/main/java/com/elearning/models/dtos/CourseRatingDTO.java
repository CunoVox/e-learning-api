package com.elearning.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRatingDTO {
    private int totalRatings = 0;
    private double averageRate = 0.0;
    private Map<Integer, Integer> ratingCounts = new HashMap<>();
}
