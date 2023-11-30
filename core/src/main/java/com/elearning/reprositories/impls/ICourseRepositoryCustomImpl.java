package com.elearning.reprositories.impls;

import com.elearning.entities.Category;
import com.elearning.entities.Course;
import com.elearning.entities.User;
import com.elearning.models.searchs.ParameterSearchCourse;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.reprositories.ICourseRepository;
import com.elearning.reprositories.ICourseRepositoryCustom;
import com.elearning.utils.Extensions;
import com.elearning.utils.QueryBuilderUtils;
import com.elearning.utils.StringUtils;
import com.elearning.utils.enumAttribute.EnumConnectorType;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@ExtensionMethod(Extensions.class)
public class ICourseRepositoryCustomImpl extends BaseRepositoryCustom implements ICourseRepositoryCustom {
    @Override
    public ListWrapper<Course> searchCourse(ParameterSearchCourse parameterSearchCourse) {
        List<Criteria> criteria = new ArrayList<>();
        Collection<String> courseIds = null;
        if (parameterSearchCourse.getLevel() != null) {
            criteria.add(Criteria.where("level").is(parameterSearchCourse.getLevel()));
        } else if (parameterSearchCourse.getIds().isNullOrEmpty()) {
            criteria.add(Criteria.where("level").is(1));
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
        }

        if (!parameterSearchCourse.getSlug().isBlankOrNull()) {
            criteria.add(Criteria.where("slug").is(parameterSearchCourse.getSlug().trim()));
        }

        QueryBuilderUtils.addSingleValueFilter(criteria, "name", parameterSearchCourse.getName());

        if (!parameterSearchCourse.getCreatedBy().isBlankOrNull()) {
            criteria.add(Criteria.where("createdBy").is(parameterSearchCourse.getCreatedBy().trim()));
        }

        Criteria criteriaKeywords = null;
        Criteria criteriaKeywordsExternal = null;
        if (!parameterSearchCourse.getMultiValue().isBlankOrNull()) {
            String multiValue = parameterSearchCourse.getMultiValue().trim();
            String multiValueMod = StringUtils.stripAccents(multiValue);
            String slug = StringUtils.getSlug(multiValue);

            Collection<String> searchIds = getCourseIdsByKeyword(multiValueMod);
            if (!searchIds.isNullOrEmpty()) {
                criteriaKeywordsExternal = Criteria.where("_id").in(searchIds);
            }
            criteriaKeywords = new Criteria()
                    .orOperator(
                            Criteria.where("_id").is(multiValue),
                            Criteria.where("_id").is(multiValueMod),
                            Criteria.where("nameMod").regex(multiValue, "i"),
                            Criteria.where("nameMod").regex(multiValueMod, "i"),
                            Criteria.where("requirement").regex(multiValue, "i"),
                            Criteria.where("requirement").regex(multiValueMod, "i"),
                            Criteria.where("description").regex(multiValue, "i"),
                            Criteria.where("description").regex(multiValueMod, "i"),
                            Criteria.where("slug").regex(slug, "i")
                    );
        }
        //danh mục
        if (!parameterSearchCourse.getCategoriesIds().isNullOrEmpty()) {
            List<String> ids = getCourseIdsByCategory(parameterSearchCourse.getCategoriesIds());
            courseIds = courseIds.merge(ids);
        }
        if (criteriaKeywords != null) {
            if (criteriaKeywordsExternal == null) {
                criteria.add(criteriaKeywords);
            } else {
                criteria.add(new Criteria().orOperator(criteriaKeywords, criteriaKeywordsExternal));
            }
        }

        if (null != parameterSearchCourse.getFromDate()) {
            criteria.add(Criteria.where("createdAt").gte(parameterSearchCourse.getFromDate()));
        }
        if (null != parameterSearchCourse.getToDate()) {
            criteria.add(Criteria.where("createdAt").lte(parameterSearchCourse.getToDate()));
        }
        if (courseIds != null) {
            criteria.add(Criteria.where("_id").in(courseIds));
        }

        Query query = new Query();
        query.with(Sort.by("createdAt").descending());
        query.addCriteria(new Criteria().andOperator(criteria));

        if (parameterSearchCourse.getMaxResult() == null) {
            return ListWrapper.<Course>builder()
                    .data(mongoTemplate.find(query, Course.class))
                    .build();
        }
//      Phân trang
        long totalResult;
        if (parameterSearchCourse.getMultiValue().isBlankOrNull()) {
            List<Criteria> pageableCriteria = new ArrayList<>(criteria);
            Query pageableQuery = new Query();
            pageableQuery.addCriteria(new Criteria().andOperator(pageableCriteria));
            totalResult = mongoTemplate.count(pageableQuery, Course.class);
        } else {
            List<Course> courses = mongoTemplate.find(query, Course.class);
            //Lấy khoá học cha
            Set<String> resultProductIds = new HashSet<>();
            resultProductIds.addAll(courses.toStream().filter(p -> p.getParentId().isBlankOrNull()).map(Course::getId).toList());
            //Lấy khoá học con
            resultProductIds.addAll(courses.toStream().map(Course::getParentId).filter(parentId -> !parentId.isBlankOrNull()).toList());
            totalResult = resultProductIds.size();
        }
        if (parameterSearchCourse.getStartIndex() != null && parameterSearchCourse.getStartIndex() >= 0) {
            query.skip(parameterSearchCourse.getStartIndex());
        }
        if (parameterSearchCourse.getMaxResult() > 0) {
            query.limit(parameterSearchCourse.getMaxResult());
        }

        return ListWrapper.<Course>builder()
                .total(totalResult)
                .totalPage((totalResult - 1) / parameterSearchCourse.getMaxResult() + 1)
                .currentPage(parameterSearchCourse.getStartIndex() / parameterSearchCourse.getMaxResult() + 1)
                .maxResult(parameterSearchCourse.getMaxResult())
                .data(mongoTemplate.find(query, Course.class))
                .build();
    }

    private Collection<String> getCourseIdsByKeyword(String keyword) {
        Map<String, List<String>> mapProductIds = new HashMap<>();
        //danh mục
        List<Category> productCategories = categoryRepository.findAllByNameModeLike(keyword);
        List<String> categoryIds = productCategories.stream().map(Category::getId).collect(Collectors.toList());
        if (!categoryIds.isNullOrEmpty()) {
            mapProductIds.putAll(connector.getIdRelatedObjectsById(Category.class.getAnnotation(Document.class).collection(),
                    categoryIds, Course.class.getAnnotation(Document.class).collection(),
                    EnumConnectorType.COURSE_TO_CATEGORY.name()));
        }
        if (mapProductIds.size() == 0) {
            return new ArrayList<>();
        }
        return mapProductIds.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private List<String> getCourseIdsByCategory(List<String> inputIds) {
        Map<String, List<String>> mapProductIds = new HashMap<>();
        List<Category> categories = categoryRepository.findAllByIdIn(inputIds);
        List<String> categoryLv1Ids = categories.stream().filter(c -> (c.getLevel() == 1)).map(Category::getId).collect(Collectors.toList());
        if (!categoryLv1Ids.isNullOrEmpty()) {
            categories.addAll(categoryRepository.findAllByParentIdIn(categoryLv1Ids));
        }
        List<String> categoryLv2Ids = categories.stream().filter(c -> (c.getLevel() == 2)).map(Category::getId).collect(Collectors.toList());
        if (!categoryLv2Ids.isNullOrEmpty()) {
            categories.addAll(categoryRepository.findAllByParentIdIn(categoryLv2Ids));
        }
        List<String> allCategoryIds = categories.stream().map(Category::getId).collect(Collectors.toList());
        if (!allCategoryIds.isNullOrEmpty()) {
            mapProductIds.putAll(connector.getIdRelatedObjectsById(Category.class.getAnnotation(Document.class).collection(),
                    allCategoryIds, Course.class.getAnnotation(Document.class).collection(),
                    EnumConnectorType.COURSE_TO_CATEGORY.name()));
        }
        if (mapProductIds.size() == 0) {
            return new ArrayList<>();
        }
        return mapProductIds.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public void updateCourseType(String courseId, String courseType, String updateBy) {
        Map<String, Object> map = new HashMap<>();
        map.put("courseType", courseType);
        updateAttribute(courseId, map, updateBy, Course.class);
    }

    @Override
    public void updateIsDeleted(String courseId, Boolean isDeleted, String updatedBy) {
        Map<String, Object> map = new HashMap<>();
        map.put("isDeleted", isDeleted);
        updateAttribute(courseId, map, updatedBy, Course.class);
    }
}
