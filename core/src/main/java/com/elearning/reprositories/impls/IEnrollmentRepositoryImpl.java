package com.elearning.reprositories.impls;

import com.elearning.entities.Course;
import com.elearning.entities.Enrollment;
import com.elearning.models.searchs.ParameterSearchEnrollment;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.reprositories.IEnrollmentRepositoryCustom;
import com.elearning.utils.Extensions;
import lombok.experimental.ExtensionMethod;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
@ExtensionMethod(Extensions.class)
public class IEnrollmentRepositoryImpl extends BaseRepositoryCustom implements IEnrollmentRepositoryCustom  {
    @Override
    public ListWrapper<Enrollment> searchEnrollment(ParameterSearchEnrollment parameterSearchEnrollment) {
        List<Criteria> criteria = new ArrayList<>();
        if (!parameterSearchEnrollment.getIds().isNullOrEmpty()) {
            criteria.add(Criteria.where("_id").in(parameterSearchEnrollment.getIds()));
        }
        if (!parameterSearchEnrollment.getCourseIds().isNullOrEmpty()) {
            criteria.add(Criteria.where("courseId").in(parameterSearchEnrollment.getCourseIds()));
        }
        if (!parameterSearchEnrollment.getUserIds().isNullOrEmpty()) {
            criteria.add(Criteria.where("userId").in(parameterSearchEnrollment.getUserIds()));
        }
        if(!parameterSearchEnrollment.getCurrentCourse().isBlankOrNull()){
            criteria.add(Criteria.where("currentCourse").is(parameterSearchEnrollment.getCurrentCourse()));
        }
        if(!parameterSearchEnrollment.getCurrentCourse().isBlankOrNull()){
            criteria.add(Criteria.where("pricePurchase").is(parameterSearchEnrollment.getPricePurchase()));
        }
        if(!parameterSearchEnrollment.getCurrentCourse().isBlankOrNull()){
            criteria.add(Criteria.where("percentComplete").is(parameterSearchEnrollment.getPercentComplete()));
        }
        if (parameterSearchEnrollment.getIsDeleted() != null) {
            criteria.add(Criteria.where("isDeleted").is(parameterSearchEnrollment.getIsDeleted()));
        } else {
            criteria.add(Criteria.where("isDeleted").ne(true));
        }

        Query query = new Query();
        query.with(Sort.by("createdAt").descending());
        query.addCriteria(new Criteria().andOperator(criteria));

        if (parameterSearchEnrollment.getMaxResult() == null) {
            return ListWrapper.<Enrollment>builder()
                    .data(mongoTemplate.find(query, Enrollment.class))
                    .build();
        }

        long totalResult;
        List<Criteria> pageableCriteria = new ArrayList<>(criteria);
        Query pageableQuery = new Query();
        pageableQuery.addCriteria(new Criteria().andOperator(pageableCriteria));
        totalResult = mongoTemplate.count(pageableQuery, Enrollment.class);
        if (parameterSearchEnrollment.getStartIndex() != null && parameterSearchEnrollment.getStartIndex() >= 0) {
            query.skip(parameterSearchEnrollment.getStartIndex());
        }
        if (parameterSearchEnrollment.getMaxResult() > 0) {
            query.limit(parameterSearchEnrollment.getMaxResult());
        }
        return ListWrapper.<Enrollment>builder()
                .total(totalResult)
                .totalPage((totalResult - 1) / parameterSearchEnrollment.getMaxResult() + 1)
                .currentPage(parameterSearchEnrollment.getStartIndex() / parameterSearchEnrollment.getMaxResult() + 1)
                .maxResult(parameterSearchEnrollment.getMaxResult())
                .data(mongoTemplate.find(query, Enrollment.class))
                .build();
    }
}
