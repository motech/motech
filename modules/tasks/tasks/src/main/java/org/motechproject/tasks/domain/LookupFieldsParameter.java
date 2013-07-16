package org.motechproject.tasks.domain;

import java.util.List;
import java.util.Objects;

public class LookupFieldsParameter {
    private String displayName;
    private List<String> fields;

    public LookupFieldsParameter() {
        this(null, null);
    }

    public LookupFieldsParameter(String displayName, List<String> fields) {
        this.displayName = displayName;
        this.fields = fields;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, fields);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final LookupFieldsParameter other = (LookupFieldsParameter) obj;

        return Objects.equals(this.displayName, other.getDisplayName())
                && Objects.equals(this.fields, other.getFields());
    }

    @Override
    public String toString() {
        return String.format("LookupFieldsParameter{displayName='%s', fields=%s}", displayName, fields);
    }
}
