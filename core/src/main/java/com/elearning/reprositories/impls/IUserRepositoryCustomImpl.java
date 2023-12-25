package com.elearning.reprositories.impls;

import com.elearning.entities.Course;
import com.elearning.entities.User;
import com.elearning.models.searchs.ParameterSearchUser;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.reprositories.IUserRepositoryCustom;
import com.elearning.utils.Extensions;
import com.elearning.utils.StringUtils;
import com.elearning.utils.enumAttribute.EnumRole;
import com.elearning.utils.enumAttribute.EnumUserStatus;
import lombok.experimental.ExtensionMethod;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

@ExtensionMethod(Extensions.class)
public class IUserRepositoryCustomImpl extends BaseRepositoryCustom implements IUserRepositoryCustom {
    @Override
    public ListWrapper<User> searchUser(ParameterSearchUser parameterSearchUser) {
        List<Criteria> criteria = new ArrayList<>();

        if (!parameterSearchUser.getUserIds().isNullOrEmpty()) {
            criteria.add(Criteria.where("_id").in(parameterSearchUser.getUserIds()));
        }
        if (parameterSearchUser.getStatus() != null) {
            if (parameterSearchUser.getStatus().equals(EnumUserStatus.ENABLE)) {
                criteria.add(Criteria.where("isDeleted").ne(true));
            } else if (parameterSearchUser.getStatus().equals(EnumUserStatus.DISABLE)) {
                criteria.add(Criteria.where("isDeleted").is(true));
            }
        }
        //Lọc roles
        if (!parameterSearchUser.getRoles().isNullOrEmpty()) {
            criteria.add(Criteria.where("roles").in(parameterSearchUser.getRoles()));
        }
        //Lọc theo keyword
        Criteria criteriaKeywords = null;
        if (!parameterSearchUser.getMultiValue().isBlankOrNull()) {
            String multiValue = parameterSearchUser.getMultiValue().trim();
            String multiValueMod = StringUtils.stripAccents(multiValue);
            criteriaKeywords = new Criteria()
                    .orOperator(
                            Criteria.where("_id").is(multiValue),
                            Criteria.where("_id").is(multiValueMod),
                            Criteria.where("fullNameMod").regex(multiValue, "i"),
                            Criteria.where("fullNameMod").regex(multiValueMod, "i")
                    );
        }
        if (criteriaKeywords != null) {
            criteria.add(criteriaKeywords);
        }

        if (null != parameterSearchUser.getFromDate()) {
            criteria.add(Criteria.where("createdAt").gte(parameterSearchUser.getFromDate()));
        }
        if (null != parameterSearchUser.getToDate()) {
            criteria.add(Criteria.where("createdAt").lte(parameterSearchUser.getToDate()));
        }

        Query query = new Query();
        query.with(Sort.by("createdAt").descending());
        if (criteria.size() > 0) {
            query.addCriteria(new Criteria().andOperator(criteria));
        }

        if (parameterSearchUser.getMaxResult() == null) {
            return ListWrapper.<User>builder()
                    .data(mongoTemplate.find(query, User.class))
                    .build();
        }
//      Phân trang
        long totalResult;
        if (parameterSearchUser.getMultiValue().isBlankOrNull()) {
            List<Criteria> pageableCriteria = new ArrayList<>(criteria);
            Query pageableQuery = new Query();
            if (pageableCriteria.size() > 0) {
                pageableQuery.addCriteria(new Criteria().andOperator(pageableCriteria));
            }
            totalResult = mongoTemplate.count(pageableQuery, User.class);
        } else {
            List<User> users = mongoTemplate.find(query, User.class);
            totalResult = users.size();
        }
        if (parameterSearchUser.getStartIndex() != null && parameterSearchUser.getStartIndex() >= 0) {
            query.skip(parameterSearchUser.getStartIndex());
        }
        if (parameterSearchUser.getMaxResult() > 0) {
            query.limit(parameterSearchUser.getMaxResult());
        }

        return ListWrapper.<User>builder()
                .total(totalResult)
                .totalPage((totalResult - 1) / parameterSearchUser.getMaxResult() + 1)
                .currentPage(parameterSearchUser.getStartIndex() / parameterSearchUser.getMaxResult() + 1)
                .maxResult(parameterSearchUser.getMaxResult())
                .data(mongoTemplate.find(query, User.class))
                .build();
    }

    @Override
    public void updateDeleted(String id, boolean deleted, String updateBy) {
        Map<String, Object> map = new HashMap<>();
        map.put("isDeleted", deleted);
        updateAttribute(id, map, updateBy, User.class);
    }

    @Override
    public void updateUserRoles(String id, List<EnumRole> roles, String updatedBy) {
        Map<String, Object> map = new HashMap<>();
        map.put("roles", roles);
        updateAttribute(id, map, updatedBy, User.class);
    }
}
