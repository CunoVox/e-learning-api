package com.elearning.reprositories;

import com.elearning.entities.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IInvoiceRepository extends MongoRepository<Invoice, String>, IInvoiceRepositoryCustom {
    Invoice findByCourseIdAndCustomerId(String courseId, String customerId);
}
