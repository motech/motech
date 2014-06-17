package org.motechproject.mds.util;


/**
 * Represents an object reference. It holds an information about related field name,
 * as well as the object that the field should reference to.
 */
public class ObjectReference {
    private String fieldName;
    private Object reference;

    public ObjectReference(String fieldName, Object reference) {
        this.fieldName = fieldName;
        this.reference = reference;
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

}
