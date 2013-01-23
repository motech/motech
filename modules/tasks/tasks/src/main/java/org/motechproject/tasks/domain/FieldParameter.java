package org.motechproject.tasks.domain;

import java.util.Objects;

public class FieldParameter extends Parameter {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FieldParameter)) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        FieldParameter that = (FieldParameter) o;

        return Objects.equals(fieldKey, that.fieldKey);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fieldKey != null ? fieldKey.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("FieldParameter{fieldKey='%s'} %s", fieldKey, super.toString());
    }
}
