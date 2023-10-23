package com.elearning.reprositories.impls;

import com.elearning.entities.Course;
import com.elearning.models.searchs.ParameterSearchCourse;
import com.elearning.reprositories.ICourseRepositoryCustom;
import com.elearning.utils.Extensions;
import lombok.experimental.ExtensionMethod;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtensionMethod(Extensions.class)
public class ICourseRepositoryCustomImpl extends BaseRepositoryCustom implements ICourseRepositoryCustom {
    @Override
    public List<Course> searchCourse(ParameterSearchCourse parameterSearchCourse) {
        List<Criteria> criteria = new ArrayList<>();

        if (parameterSearchCourse.getLevel() != null) {
            criteria.add(Criteria.where("level").is(parameterSearchCourse.getLevel()));
        }
        if (!parameterSearchCourse.getParentIds().isNullOrEmpty()) {
            criteria.add(Criteria.where("parentId").in(parameterSearchCourse.getParentIds()));
        }

        if (!parameterSearchCourse.getIds().isNullOrEmpty()) {
            criteria.add(Criteria.where("_id").in(parameterSearchCourse.getIds()));
        }

        if (parameterSearchCourse.getSearchType() != null) {
            criteria.add(Criteria.where("courseType").is(parameterSearchCourse.getSearchType()));
        }
        if (parameterSearchCourse.getIsDeleted() != null) {
            criteria.add(Criteria.where("isDeleted").is(parameterSearchCourse.getIsDeleted()));
        } else {
            criteria.add(Criteria.where("isDeleted").ne(true));
        }

        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(criteria));
        return mongoTemplate.find(query, Course.class);
    }

    @Override
    public void updateCourseType(String courseId, String courseType, String updateBy) {
        Map<String, Object> map = new HashMap<>();
        map.put("courseType", courseType);
        updateAttribute(courseId, map, updateBy, Course.class);
    }
}
