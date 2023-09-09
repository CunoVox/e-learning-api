package com.elearning.connector;

import com.elearning.utils.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Stream;

public class QueryBuilderUtils {
    public QueryBuilderUtils() {
    }

    public static void addDateFilter(Collection<Criteria> filterList, String fieldName, Date startDate, Date endDate) {
        Criteria criteria;
        if (startDate != null && endDate != null) {
            criteria = Criteria.where(fieldName).gte(startDate).lte(endDate);
            filterList.add(criteria);
        } else if (startDate != null) {
            criteria = Criteria.where(fieldName).gte(startDate);
            filterList.add(criteria);
        } else if (endDate != null) {
            criteria = Criteria.where(fieldName).lte(endDate);
            filterList.add(criteria);
        }

    }

    public static void addDateFilterInRange(Collection<Criteria> filterList, DateInRangeSearchRequest request) {
        if (Objects.nonNull(request) && request.getDate() != null) {
            Criteria criteria;
            if (!StringUtils.isNullOrEmptyString(request.getFromDateField()) && !StringUtils.isNullOrEmptyString(request.getToDateField())) {
                criteria = Criteria.where(request.getFromDateField()).lte(request.getDate()).andOperator(new Criteria[]{Criteria.where(request.getToDateField()).gte(request.getDate())});
                filterList.add(criteria);
            } else if (!StringUtils.isNullOrEmptyString(request.getFromDateField())) {
                criteria = Criteria.where(request.getFromDateField()).lte(request.getDate());
                filterList.add(criteria);
            } else if (!StringUtils.isNullOrEmptyString(request.getToDateField())) {
                criteria = Criteria.where(request.getToDateField()).gte(request.getDate());
                filterList.add(criteria);
            }
        }

    }

    public static void addSingleRegexSearch(Collection<Criteria> filterList, String fieldName, String value) {
        if (!StringUtils.isNullOrEmptyString(value)) {
            Criteria criteria = Criteria.where(fieldName).regex(value, "i");
            filterList.add(criteria);
        }

    }

    public static void addSingleValueFilter(Collection<Criteria> filterList, String fieldName, Object value) {
        if (Objects.nonNull(value) && !value.toString().isEmpty()) {
            Criteria criteria = Criteria.where(fieldName).is(value);
            filterList.add(criteria);
        }

    }

    public static void addMultipleValuesFilter(Collection<Criteria> filterList, String fieldName, Collection<String> value) {
        if (!CollectionUtils.isEmpty(value)) {
            Criteria criteria = Criteria.where(fieldName).in(value).ne("");
            filterList.add(criteria);
        }

    }

    public static void addSingleValuesNinFilter(Collection<Criteria> filterList, String fieldName, Collection<String> value) {
        if (!CollectionUtils.isEmpty(value)) {
            Criteria criteria = Criteria.where(fieldName).nin(value);
            filterList.add(criteria);
        }

    }

    public static void addFilterNullValue(Collection<Criteria> filterList, String fieldName, Boolean isNull) {
        if (isNull) {
            Criteria criteria = Criteria.where(fieldName).is((Object)null);
            filterList.add(criteria);
        }

    }

    public static void addSingleValueNeFilter(Collection<Criteria> filterList, String fieldName, String value) {
        Criteria criteria = Criteria.where(fieldName).ne(value);
        filterList.add(criteria);
    }

    public static void orOperator(Criteria criteria, Collection<Criteria> criteriaList) {
        if (!CollectionUtils.isEmpty(criteriaList)) {
            criteria.orOperator(criteriaList);
        }

    }

    public static void orOperator(Criteria criteria, Criteria... criteriaList) {
        if (Objects.nonNull(criteriaList)) {
            criteria.orOperator(criteriaList);
        }

    }

    public static void andOperator(Criteria criteria, Collection<Criteria> filterList) {
        if (!CollectionUtils.isEmpty(filterList)) {
            criteria.andOperator(filterList);
        }

    }

    public static void andOperator(Criteria criteria, Criteria... filterList) {
        if (Objects.nonNull(filterList)) {
            criteria.andOperator(filterList);
        }

    }

    public static void addPageable(Query query, @NotNull Pageable pageable, @NotNull Boolean isPageAble) {
        if (isPageAble) {
            query.with(pageable);
        } else {
            pageable = Pageable.unpaged();
        }

    }
}
