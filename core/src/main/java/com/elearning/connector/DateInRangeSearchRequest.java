package com.elearning.connector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class DateInRangeSearchRequest {
    private Date date;
    private String fromDateField;
    private String toDateField;

    public static DateInRangeSearchRequest.DateInRangeSearchRequestBuilder builder() {
        return new DateInRangeSearchRequest.DateInRangeSearchRequestBuilder();
    }

    public Date getDate() {
        return this.date;
    }

    public String getFromDateField() {
        return this.fromDateField;
    }

    public String getToDateField() {
        return this.toDateField;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public void setFromDateField(final String fromDateField) {
        this.fromDateField = fromDateField;
    }

    public void setToDateField(final String toDateField) {
        this.toDateField = toDateField;
    }

    public DateInRangeSearchRequest() {
    }

    public DateInRangeSearchRequest(final Date date, final String fromDateField, final String toDateField) {
        this.date = date;
        this.fromDateField = fromDateField;
        this.toDateField = toDateField;
    }

    public static class DateInRangeSearchRequestBuilder {
        private Date date;
        private String fromDateField;
        private String toDateField;

        DateInRangeSearchRequestBuilder() {
        }

        public DateInRangeSearchRequest.DateInRangeSearchRequestBuilder date(final Date date) {
            this.date = date;
            return this;
        }

        public DateInRangeSearchRequest.DateInRangeSearchRequestBuilder fromDateField(final String fromDateField) {
            this.fromDateField = fromDateField;
            return this;
        }

        public DateInRangeSearchRequest.DateInRangeSearchRequestBuilder toDateField(final String toDateField) {
            this.toDateField = toDateField;
            return this;
        }

        public DateInRangeSearchRequest build() {
            return new DateInRangeSearchRequest(this.date, this.fromDateField, this.toDateField);
        }

        public String toString() {
            return "DateInRangeSearchRequest.DateInRangeSearchRequestBuilder(date=" + this.date + ", fromDateField=" + this.fromDateField + ", toDateField=" + this.toDateField + ")";
        }
    }
}

