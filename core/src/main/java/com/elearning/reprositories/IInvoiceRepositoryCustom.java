package com.elearning.reprositories;

import com.elearning.entities.Invoice;
import com.elearning.models.searchs.ParameterSearchInvoice;
import com.elearning.models.wrapper.ListWrapper;

import java.util.List;

public interface IInvoiceRepositoryCustom {

    ListWrapper<Invoice> searchInvoice(ParameterSearchInvoice parameterSearchInvoice);
}
