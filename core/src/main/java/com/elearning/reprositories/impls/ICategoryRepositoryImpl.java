package com.elearning.reprositories.impls;

import com.elearning.entities.Category;
import com.elearning.models.searchs.ParameterSearchCategory;
import com.elearning.reprositories.ICategoryRepositoryCustom;
import com.elearning.utils.Extensions;
import lombok.experimental.ExtensionMethod;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@ExtensionMethod(Extensions.class)
public class ICategoryRepositoryImpl extends BaseRepositoryCustom implements ICategoryRepositoryCustom {

    @Override
    public List<Category> searchCategories(ParameterSearchCategory parameterSearchCategory) {
        List<Criteria> criteria = new ArrayList<>();

        if (parameterSearchCategory.getLevel() != null) {
            criteria.add(Criteria.where("level").is(parameterSearchCategory.getLevel()));
        }
        if (!parameterSearchCategory.getCategoriesIds().isNullOrEmpty()) {
            criteria.add(Criteria.where("_id").in(parameterSearchCategory.getCategoriesIds()));
        }

        if (!parameterSearchCategory.getParentIds().isNullOrEmpty()) {
            criteria.add(Criteria.where("parentId").in(parameterSearchCategory.getParentIds()));
        }

        if (parameterSearchCategory.getIsDeleted() != null && parameterSearchCategory.getIsDeleted()) {
            criteria.add(Criteria.where("isDeleted").is(true));
        } else if (parameterSearchCategory.getIsDeleted() != null) {
            criteria.add(Criteria.where("isDeleted").ne(true));
        }
        Query query = new Query();
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        return mongoTemplate.find(query, Category.class);
    }
}
