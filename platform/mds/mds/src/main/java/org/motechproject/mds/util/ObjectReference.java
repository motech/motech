package org.motechproject.mds.util;


/**
 * Represents an object reference. It holds an information about related field name,
 * as well as the object that the field should reference to.
 */
public class ObjectReference {
    private String fieldName;
    private Object reference;
    private String mappingFieldName;

    public ObjectReference(String fieldName, Object reference, String mappingFieldName) {
        this.fieldName = fieldName;
        this.reference = reference;
        this.mappingFieldName = mappingFieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getReference() {
        return reference;
    }

    public void setReference(Object reference) {
        this.reference = reference;
    }

    public String getMappingFieldName() {
        return mappingFieldName;
    }

    public void setMappingFieldName(String mappingFieldName) {
        this.mappingFieldName = mappingFieldName;
    }
}
