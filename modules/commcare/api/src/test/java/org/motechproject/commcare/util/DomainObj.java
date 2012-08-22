package org.motechproject.commcare.util;

import org.joda.time.DateTime;

public class DomainObj {
    private String field1;
    private String field2;
    private DateTime dateField;

    public DateTime getDateField() {
        return dateField;
    }

    public void setDateField(DateTime dateField) {
        this.dateField = dateField;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }
}
