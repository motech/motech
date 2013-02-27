package org.motechproject.tasks.domain;

import java.util.Objects;

public class ActionParameter extends Parameter {
    private static final long serialVersionUID = 8204529887802399508L;

    private String key;

    public ActionParameter() {
        this(null, null);
    }

    public ActionParameter(String displayName, String key) {
        this(displayName, key, ParameterType.UNICODE);
    }

    public ActionParameter(final String displayName, final String key, final ParameterType type) {
        super(displayName, type);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
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

        final ActionParameter other = (ActionParameter) obj;

        return Objects.equals(this.key, other.key);
    }

    @Override
    public String toString() {
        return String.format("ActionParameter{key='%s'} %s", key, super.toString());
    }
}
