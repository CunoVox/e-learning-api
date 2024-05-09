package com.elearning.reprositories.impls;

import com.elearning.entities.Course;
import com.elearning.entities.Rating;
import com.elearning.reprositories.IRatingRepositoryCustom;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumCourseType;
import lombok.experimental.ExtensionMethod;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtensionMethod(Extensions.class)

public class IRatingRepositoryCustomImpl  extends BaseRepositoryCustom implements IRatingRepositoryCustom {
    @Override
    public Map<String, Double> avgRattingByCourseCreatedByIn(List<String> createdBy) {
        List<Course> courses = mongoTemplate.find(
                Query.query(
                        Criteria.where("createdBy").in(createdBy)
                                .and("courseType").in(List.of(EnumCourseType.OFFICIAL.name(), EnumCourseType.CHANGE_PRICE.name()))
                                .and("level").is(1)
                                .and("isDeleted").nin(true)
                ),
                Course.class);
        Map<String, Double> result = new HashMap<>();
        List<Rating> ratings = mongoTemplate.find(
                Query.query(Criteria.where("courseId").in(courses.stream().map(Course::getId).toList())),
                Rating.class);
        for (Course course : courses) {
            List<Rating> ratingList = ratings.stream().filter(rating -> rating.getCourseId().equals(course.getId())).toList();
            if (!ratingList.isEmpty()) {
                double avg = ratingList.stream().mapToDouble(Rating::getRate).average().orElse(0);
                result.put(course.getCreatedBy(), avg);
            }
        }
        return result;
    }
}
