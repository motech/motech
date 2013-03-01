package org.motechproject.tasks.domain;

import java.util.Objects;

public class FieldParameter extends Parameter {
    private static final long serialVersionUID = -2789552939112269521L;

    private String fieldKey;

    public FieldParameter() {
        this(null, null);
    }

    public FieldParameter(String displayName, String fieldKey) {
        this(displayName, fieldKey, ParameterType.UNICODE);
    }

    public FieldParameter(final String displayName, final String fieldKey, final ParameterType type) {
        super(displayName, type);
        this.fieldKey = fieldKey;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(final String fieldKey) {
        this.fieldKey = fieldKey;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final FieldParameter other = (FieldParameter) obj;

        return Objects.equals(this.fieldKey, other.fieldKey);
    }

    @Override
    public String toString() {
        return String.format("FieldParameter{fieldKey='%s'} %s", fieldKey, super.toString());
    }
}
