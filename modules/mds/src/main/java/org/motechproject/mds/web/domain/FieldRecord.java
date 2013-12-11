package org.motechproject.mds.web.domain;

/**
 * Represents single field of entity instance
 */
public class FieldRecord {
    private String name;
    private String displayName;
    private Object value;

    public FieldRecord() {
        this(null, null, null);
    }

    public FieldRecord(String name, String displayName, Object value) {
        this.name = name;
        this.displayName = displayName;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
