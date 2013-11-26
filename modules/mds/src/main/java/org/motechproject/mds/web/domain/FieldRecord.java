package org.motechproject.mds.web.domain;

/**
 * Represents single field of entity instance
 */
public class FieldRecord {
    private String displayName;
    private Object value;

    public FieldRecord() {
        this(null, null);
    }

    public FieldRecord(String name, Object value) {
        this.displayName = name;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
