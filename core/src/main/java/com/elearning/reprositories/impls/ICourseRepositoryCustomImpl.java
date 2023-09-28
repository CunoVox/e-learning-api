package com.elearning.reprositories.impls;

import com.elearning.entities.Course;
import com.elearning.models.searchs.ParameterSearchCourse;
import com.elearning.reprositories.ICourseRepositoryCustom;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumCourseType;
import lombok.experimental.ExtensionMethod;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

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

        if (!parameterSearchCourse.getCategoriesIds().isNullOrEmpty()) {
            criteria.add(Criteria.where("_id").in(parameterSearchCourse.getCategoriesIds()));
        }

        if (parameterSearchCourse.getIsDraft() != null && parameterSearchCourse.getIsDraft()) {
            criteria.add(Criteria.where("courseType").is(EnumCourseType.DRAFT.name()));
        } else {
            criteria.add(Criteria.where("courseType").is(EnumCourseType.OFFICIAL.name()));
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
}
