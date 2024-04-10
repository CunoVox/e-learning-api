package com.elearning.reprositories.impls;

import com.elearning.entities.Comment;
import com.elearning.entities.Enrollment;
import com.elearning.models.searchs.ParameterSearchComment;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.reprositories.ICommentRepositoryCustom;
import com.elearning.utils.Extensions;
import lombok.experimental.ExtensionMethod;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
@ExtensionMethod(Extensions.class)
public class ICommentRepositoryCustomImpl  extends BaseRepositoryCustom implements ICommentRepositoryCustom {
    @Override
    public ListWrapper<Comment> searchComments(ParameterSearchComment parameterSearchComment) {
        List<Criteria> criteria = new ArrayList<>();
//        if (parameterSearchComment.getLevel() != null) {
//            criteria.add(Criteria.where("level").is(parameterSearchComment.getLevel()));
//        }
//        if (!parameterSearchComment.getIds().isNullOrEmpty()) {
//            criteria.add(Criteria.where("_id").in(parameterSearchComment.getIds()));
//        }
//        if (!parameterSearchComment.getCourseIds().isNullOrEmpty()) {
//            criteria.add(Criteria.where("courseId").in(parameterSearchComment.getCourseIds()));
//        }
//        if (!parameterSearchComment.getUserIds().isNullOrEmpty()) {
//            criteria.add(Criteria.where("userId").in(parameterSearchComment.getUserIds()));
//        }
//        if(!parameterSearchComment.getCurrentCourse().isBlankOrNull()){
//            criteria.add(Criteria.where("currentCourse").is(parameterSearchComment.getCurrentCourse()));
//        }
//        if(!parameterSearchComment.getCurrentCourse().isBlankOrNull()){
//            criteria.add(Criteria.where("pricePurchase").is(parameterSearchComment.getPricePurchase()));
//        }
//        if(!parameterSearchComment.getCurrentCourse().isBlankOrNull()){
//            criteria.add(Criteria.where("percentComplete").is(parameterSearchComment.getPercentComplete()));
//        }
        if (parameterSearchComment.getType() != null) {
            criteria.add(Criteria.where("type").is(parameterSearchComment.getType()));
        }
        if(!parameterSearchComment.getReferenceId().isBlankOrNull()){
            criteria.add(Criteria.where("referenceId").is(parameterSearchComment.getReferenceId()));
        }
        if (parameterSearchComment.getIsDeleted() != null) {
            criteria.add(Criteria.where("isDeleted").is(parameterSearchComment.getIsDeleted()));
        } else {
            criteria.add(Criteria.where("isDeleted").ne(true));
        }

        Query query = new Query();
        query.with(Sort.by("createdAt").descending());
        query.addCriteria(new Criteria().andOperator(criteria));

        if (parameterSearchComment.getMaxResult() == null) {
            return ListWrapper.<Comment>builder()
                    .data(mongoTemplate.find(query, Comment.class))
                    .build();
        }

        long totalResult;
        List<Criteria> pageableCriteria = new ArrayList<>(criteria);
        Query pageableQuery = new Query();
        pageableQuery.addCriteria(new Criteria().andOperator(pageableCriteria));
        totalResult = mongoTemplate.count(pageableQuery, Enrollment.class);
        if (parameterSearchComment.getStartIndex() != null && parameterSearchComment.getStartIndex() >= 0) {
            query.skip(parameterSearchComment.getStartIndex());
        }
        if (parameterSearchComment.getMaxResult() > 0) {
            query.limit(parameterSearchComment.getMaxResult());
        }
        return ListWrapper.<Comment>builder()
                .total(totalResult)
                .totalPage((totalResult - 1) / parameterSearchComment.getMaxResult() + 1)
                .currentPage(parameterSearchComment.getStartIndex() / parameterSearchComment.getMaxResult() + 1)
                .maxResult(parameterSearchComment.getMaxResult())
                .data(mongoTemplate.find(query, Comment.class))
                .build();
    }
}
