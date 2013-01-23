package org.motechproject.tasks.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskDataProviderObject {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskDataProviderObject that = (TaskDataProviderObject) o;

        return Objects.equals(displayName, that.displayName) && Objects.equals(fields, that.fields) &&
                Objects.equals(type, that.type) && Objects.equals(lookupFields, that.lookupFields);
    }

    @Override
    public int hashCode() {
        int result = displayName != null ? displayName.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (lookupFields != null ? lookupFields.hashCode() : 0);
        result = 31 * result + (fields != null ? fields.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("TaskDataProviderObject{displayName='%s', type='%s', lookupFields=%s, fields=%s}",
                displayName, type, lookupFields, fields);
    }
}
