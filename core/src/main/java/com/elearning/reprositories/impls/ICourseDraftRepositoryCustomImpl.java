package com.elearning.reprositories.impls;

import com.elearning.entities.Category;
import com.elearning.entities.CourseDraft;
import com.elearning.models.searchs.ParameterSearchCourseDraft;
import com.elearning.reprositories.ICourseDraftRepositoryCustom;
import com.elearning.reprositories.ICourseRepositoryCustom;
import com.elearning.utils.Extensions;
import lombok.experimental.ExtensionMethod;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
@ExtensionMethod(Extensions.class)
public class ICourseDraftRepositoryCustomImpl extends BaseRepositoryCustom implements ICourseDraftRepositoryCustom {
    @Override
    public List<CourseDraft> searchCourseDraft(ParameterSearchCourseDraft parameterSearchCourseDraft) {
        List<Criteria> criteria = new ArrayList<>();

        if (parameterSearchCourseDraft.getLevel() != null) {
            criteria.add(Criteria.where("level").is(parameterSearchCourseDraft.getLevel()));
        }

        if (!parameterSearchCourseDraft.getCategoriesIds().isNullOrEmpty()) {
            criteria.add(Criteria.where("_id").in(parameterSearchCourseDraft.getCategoriesIds()));
        }

        if (!parameterSearchCourseDraft.getParentIds().isNullOrEmpty()) {
            criteria.add(Criteria.where("parentId").in(parameterSearchCourseDraft.getParentIds()));
        }

        if (parameterSearchCourseDraft.getIsDeleted() != null) {
            criteria.add(Criteria.where("isDeleted").is(parameterSearchCourseDraft.getIsDeleted()));
        }
        else {
            criteria.add(Criteria.where("isDeleted").ne(true));
        }

        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(criteria));
        return mongoTemplate.find(query, CourseDraft.class);
    }
}
