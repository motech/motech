package org.motechproject.commons.couchdb.dao;

public class BusinessIdNotUniqueException extends RuntimeException {
    private String fieldName;
    private String id;

    public String getFieldName() {
        return fieldName;
    }

    public String getId() {
        return id;
    }

    public BusinessIdNotUniqueException(String fieldName, String id) {
        this.fieldName = fieldName;
        this.id = id;
    }
}
