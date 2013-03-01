package org.motechproject.tasks.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskDataProviderObject implements Serializable {
    private static final long serialVersionUID = 1767818631190935233L;

    private String displayName;
    private String type;
    private List<String> lookupFields;
    private List<FieldParameter> fields;

    public TaskDataProviderObject() {
        this(null, null, new ArrayList<String>(), new ArrayList<FieldParameter>());
    }

    public TaskDataProviderObject(String displayName, String type, List<String> lookupFields, List<FieldParameter> fields) {
        this.displayName = displayName;
        this.type = type;
        this.lookupFields = lookupFields;
        this.fields = fields;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLookupFields() {
        return lookupFields;
    }

    public void setLookupFields(List<String> lookupFields) {
        this.lookupFields = lookupFields;
    }

    public List<FieldParameter> getFields() {
        return fields;
    }

    public void setFields(List<FieldParameter> fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TaskDataProviderObject other = (TaskDataProviderObject) obj;

        return Objects.equals(this.displayName, other.displayName) &&
                Objects.equals(this.type, other.type) &&
                Objects.equals(this.lookupFields, other.lookupFields) &&
                Objects.equals(this.fields, other.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, type, lookupFields, fields);
    }

    @Override
    public String toString() {
        return String.format("TaskDataProviderObject{displayName='%s', type='%s', lookupFields=%s, fields=%s}",
                displayName, type, lookupFields, fields);
    }
}
