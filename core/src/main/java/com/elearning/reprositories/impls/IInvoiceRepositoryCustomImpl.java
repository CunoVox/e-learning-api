package com.elearning.reprositories.impls;

import com.elearning.entities.Course;
import com.elearning.entities.Invoice;
import com.elearning.models.searchs.ParameterSearchInvoice;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.reprositories.ICourseRepository;
import com.elearning.reprositories.IInvoiceRepositoryCustom;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumSortCourse;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ExtensionMethod(Extensions.class)
public class IInvoiceRepositoryCustomImpl extends BaseRepositoryCustom implements IInvoiceRepositoryCustom {
    @Autowired
    private ICourseRepository courseRepository;
    @Override
    public ListWrapper<Invoice> searchInvoice(ParameterSearchInvoice parameterSearchInvoice) {
        List<Criteria> criteria = new ArrayList<>();
        List<String> courseIds = new ArrayList<>();
        if (!parameterSearchInvoice.getSellerId().isBlankOrNull()) {
            List<Course> courses = courseRepository.findAllByCreatedByInAndLevel(Collections.singletonList(parameterSearchInvoice.getSellerId()), 1);
            if (!courses.isNullOrEmpty()) {
                courseIds.addAll(courses.stream().map(Course::getId).collect(Collectors.toList()));
            }
        }
        if (null != parameterSearchInvoice.getFromDate()) {
            criteria.add(Criteria.where("createdAt").gte(parameterSearchInvoice.getFromDate()));
        }
        if (null != parameterSearchInvoice.getToDate()) {
            criteria.add(Criteria.where("createdAt").lte(parameterSearchInvoice.getToDate()));
        }
        if (!courseIds.isNullOrEmpty()) {
            criteria.add(Criteria.where("courseId").in(courseIds));
        }
        Query query = new Query();
        query.with(Sort.by("createdAt").descending());
        if (!criteria.isEmpty()){
            query.addCriteria(new Criteria().andOperator(criteria));
        }

        if (parameterSearchInvoice.getMaxResult() == null) {
            return ListWrapper.<Invoice>builder()
                    .data(mongoTemplate.find(query, Invoice.class))
                    .total(mongoTemplate.count(query, Invoice.class))
                    .build();
        }
        if (parameterSearchInvoice.getCurrentPage() == null) {
            parameterSearchInvoice.setCurrentPage(1);
        }
        return ListWrapper.<Invoice>builder()
                .data(mongoTemplate.find(query.skip((long) (parameterSearchInvoice.getCurrentPage() - 1) * parameterSearchInvoice.getMaxResult()).limit(parameterSearchInvoice.getMaxResult()), Invoice.class))
                .total(mongoTemplate.count(query, Invoice.class))
                .currentPage(parameterSearchInvoice.getCurrentPage())
                .maxResult(parameterSearchInvoice.getMaxResult())
                .build();
    }
}
