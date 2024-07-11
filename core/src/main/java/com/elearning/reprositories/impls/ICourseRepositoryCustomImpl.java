package com.elearning.reprositories.impls;

import com.elearning.entities.Category;
import com.elearning.entities.Course;
import com.elearning.entities.IBaseEntity;
import com.elearning.models.searchs.ParameterSearchCourse;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.reprositories.ICourseRepositoryCustom;
import com.elearning.utils.Extensions;
import com.elearning.utils.QueryBuilderUtils;
import com.elearning.utils.StringUtils;
import com.elearning.utils.enumAttribute.EnumConnectorType;
import com.elearning.utils.enumAttribute.EnumCourseType;
import com.elearning.utils.enumAttribute.EnumSortCourse;
import lombok.experimental.ExtensionMethod;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.Field;
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

//        if (parameterSearchCourse.getSearchType() != null && !parameterSearchCourse.getSearchType().equals(EnumCourseType.OFFICIAL.name())) {
//            criteria.add(Criteria.where("courseType").is(parameterSearchCourse.getSearchType()));
//        } else if (parameterSearchCourse.getSearchType() == null) {
//            criteria.add(Criteria.where("courseType").in(EnumCourseType.OFFICIAL.name(), EnumCourseType.CHANGE_PRICE.name()));
//        }
        if(!parameterSearchCourse.getSearchType().isNullOrEmpty()){
            criteria.add(Criteria.where("courseType").in(parameterSearchCourse.getSearchType()));
        }else{
            criteria.add(Criteria.where("courseType").in(EnumCourseType.OFFICIAL.name(), EnumCourseType.CHANGE_PRICE.name()));
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

        //sort theo rating
        if (!parameterSearchCourse.getSortBy().isBlankOrNull()) {
            if (parameterSearchCourse.getSortBy().equals(EnumSortCourse.HIGHEST_RATING.name())) {
                Aggregation aggregation = Aggregation.newAggregation(
                        Aggregation.match(new Criteria().andOperator(criteria)),
                        Aggregation.lookup("rating", "_id", "courseId", "rating"),
                        Aggregation.unwind("rating", true),
                        Aggregation.group("_id")
                                .first("partnerId").as("partnerId")
                                .first("courseType").as("courseType")
                                .first("status").as("status")
                                .first("name").as("name")
                                .first("nameMod").as("nameMod")
                                .first("slug").as("slug")
                                .first("contentType").as("contentType")
                                .first("parentId").as("parentId")
                                .first("level").as("level")
                                .first("description").as("description")
                                .first("requirement").as("requirement")
                                .first("duration").as("duration")
                                .first("subscriptions").as("subscriptions")
                                .first("isPublished").as("isPublished")
                                .first("attributes").as("attributes")
                                .first("createdAt").as("createdAt")
                                .first("updatedAt").as("updatedAt")
                                .first("createdBy").as("createdBy")
                                .first("updatedBy").as("updatedBy")
                                .first("isDeleted").as("isDeleted")
                                .avg("rating.rate").as("averageRating")
                                .count().as("totalRating"),
                        Aggregation.sort(Sort.Direction.DESC, "averageRating", "totalRating"),
                        Aggregation.skip(parameterSearchCourse.getStartIndex()),
                        Aggregation.limit(parameterSearchCourse.getMaxResult())
                );
                AggregationResults<Course> results = mongoTemplate.aggregate(aggregation, Course.class, Course.class);
                List<Course> courses = results.getMappedResults();
                return ListWrapper.<Course>builder()
                        .total(courses.size())
                        .totalPage((courses.size() - 1) / parameterSearchCourse.getMaxResult() + 1)
                        .currentPage(parameterSearchCourse.getStartIndex() / parameterSearchCourse.getMaxResult() + 1)
                        .maxResult(parameterSearchCourse.getMaxResult())
                        .data(courses)
                        .build();
            }

            //sort theo giá PRICE_DESC
            if (parameterSearchCourse.getSortBy().equals(EnumSortCourse.PRICE.name()) && !parameterSearchCourse.getSortOrder().isBlankOrNull()) {
                MatchOperation matchPartyId = Aggregation.match(new Criteria().andOperator(criteria));
                LookupOperation lookupOperation = Aggregation.lookup("price", "_id", "parentId", "prices");
                UnwindOperation unwindOperation = Aggregation.unwind("prices");
                ProjectionOperation projectionOperation = Aggregation.project(Fields.UNDERSCORE_ID)
                        .and("partnerId").as("partnerId")
                        .and("courseType").as("courseType")
                        .and("status").as("status")
                        .and("name").as("name")
                        .and("nameMode").as("nameMode")
                        .and("slug").as("slug")
                        .and("contentType").as("contentType")
                        .and("parentId").as("parentId")
                        .and("level").as("level")
                        .and("description").as("description")
                        .and("shortDescription").as("shortDescription")
                        .and("isPreview").as("isPreview")
                        .and("requirement").as("requirement")
                        .and("duration").as("duration")
                        .and("subscriptions").as("subscriptions")
                        .and("attributes").as("attributes")
                        .and("createdAt").as("createdAt")
                        .and("updatedAt").as("updatedAt")
                        .and("createdBy").as("createdBy")
                        .and("updatedBy").as("updatedBy")
                        .and("isDeleted").as("isDeleted")
                        .and("prices").as("prices")
                        .and(ConditionalOperators.when(Criteria.where("prices.price").ne(null))
                                .then("$prices.price")
                                .otherwise(parameterSearchCourse.getSortOrder().equals("ASC") ? Integer.MAX_VALUE : Integer.MIN_VALUE))
                        .as("priceSell");
                GroupOperation groupOperation = Aggregation.group(Fields.UNDERSCORE_ID)
                        .first("partnerId").as("partnerId")
                        .first("courseType").as("courseType")
                        .first("status").as("status")
                        .first("name").as("name")
                        .first("nameMode").as("nameMode")
                        .first("slug").as("slug")
                        .first("contentType").as("contentType")
                        .first("parentId").as("parentId")
                        .first("level").as("level")
                        .first("description").as("description")
                        .first("shortDescription").as("shortDescription")
                        .first("isPreview").as("isPreview")
                        .first("requirement").as("requirement")
                        .first("duration").as("duration")
                        .first("subscriptions").as("subscriptions")
                        .first("attributes").as("attributes")
                        .first("createdAt").as("createdAt")
                        .first("updatedAt").as("updatedAt")
                        .first("createdBy").as("createdBy")
                        .first("updatedBy").as("updatedBy")
                        .first("isDeleted").as("isDeleted")
                        .max("priceSell").as("priceSell");
                SortOperation sortOperation;
                if (parameterSearchCourse.getSortOrder().equals("ASC")) {
                    sortOperation = Aggregation.sort(Sort.by(
                            Sort.Order.asc("priceSell").nullsLast(),
                            Sort.Order.desc("createdAt")
                    ));
                } else {
                    sortOperation = Aggregation.sort(Sort.by(
                            Sort.Order.desc("priceSell").nullsFirst(),
                            Sort.Order.desc("createdAt")
                    ));
                }
                SkipOperation skipOperation = null;
                LimitOperation limitOperation = null;
                if (parameterSearchCourse.getStartIndex() >= 0) {
                    skipOperation = Aggregation.skip(parameterSearchCourse.getStartIndex());
                }
                if (parameterSearchCourse.getMaxResult() > 0) {
                    limitOperation = Aggregation.limit(parameterSearchCourse.getMaxResult());
                }
                long totalResult = mongoTemplate.count(Query.query(new Criteria().andOperator(criteria)), Course.class);
                Aggregation aggregation = Aggregation.newAggregation(matchPartyId, lookupOperation, unwindOperation, projectionOperation,
                        groupOperation, sortOperation, skipOperation, limitOperation);

                AggregationResults<Course> output
                        = mongoTemplate.aggregate(aggregation,  Course.class, Course.class);

                return ListWrapper.<Course>builder()
                        .total(totalResult)
                        .totalPage((totalResult - 1) / parameterSearchCourse.getMaxResult() + 1)
                        .currentPage(parameterSearchCourse.getStartIndex() / parameterSearchCourse.getMaxResult() + 1)
                        .data(output.getMappedResults())
                        .build();
            }
        }


        Query query = new Query();

        if (!parameterSearchCourse.getSortBy().isBlankOrNull() && parameterSearchCourse.getSortBy().equals(EnumSortCourse.HIGHEST_SUB.name())) {
            //sort theo subscriptions (số lượng đăng ký) giảm dần và createdAt giảm dần
            query.with(Sort.by("subscriptions").descending().and(Sort.by("createdAt").descending()));
        }
        if (parameterSearchCourse.getSortBy().isBlankOrNull()) {
            query.with(Sort.by("createdAt").descending());
        }
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

    @Override
    public void updateIsPreview(String courseId, Boolean isPreview, String updatedBy) {
        Map<String, Object> map = new HashMap<>();
        map.put("isPreview", isPreview);
        updateAttribute(courseId, map, updatedBy, Course.class);
    }

    @Override
    public void updateCourseSubscriptions(String courseId, Long subscriptions, String updatedBy) {
        Map<String, Object> map = new HashMap<>();
        map.put("subscriptions", subscriptions);
        updateAttribute(courseId, map, updatedBy, Course.class);
    }


    @Override
    public Map<String, Long> sumSubscriptionsByCreatedBy(List<String> createdBy) {
        List<Course> courses = mongoTemplate.find(
                Query.query(
                        Criteria.where("createdBy").in(createdBy)
                                .and("courseType").in(List.of(EnumCourseType.OFFICIAL.name(), EnumCourseType.CHANGE_PRICE.name()))
                                .and("level").is(1)
                                .and("isDeleted").nin(true)
                ),
                Course.class);
        Map<String, Long> result = new HashMap<>();
        courses.forEach(course -> {
            if (result.containsKey(course.getCreatedBy())) {
                result.put(course.getCreatedBy(), result.get(course.getCreatedBy()) + (course.getSubscriptions() == null ? 0 : course.getSubscriptions()));
            } else {
                result.put(course.getCreatedBy(), course.getSubscriptions() == null ? 0 : course.getSubscriptions());
            }
        });
        return result;
    }

    @Override
    public Map<String, Integer> countAllByCreatedBy(List<String> createdBy) {
        List<Course> courses = mongoTemplate.find(
                Query.query(
                        Criteria.where("createdBy").in(createdBy)
                                .and("courseType").in(List.of(EnumCourseType.OFFICIAL.name(), EnumCourseType.CHANGE_PRICE.name()))
                                .and("level").is(1)
                                .and("isDeleted").nin(true)
                ),
                Course.class);
        Map<String, Integer> result = new HashMap<>();
        courses.forEach(course -> {
            if (result.containsKey(course.getCreatedBy())) {
                result.put(course.getCreatedBy(), result.get(course.getCreatedBy()) + 1);
            } else {
                result.put(course.getCreatedBy(), 1);
            }
        });
        return result;
    }
}
