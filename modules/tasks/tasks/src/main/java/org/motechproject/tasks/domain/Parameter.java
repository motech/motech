package org.motechproject.tasks.domain;

import java.util.Objects;

public abstract class Parameter {
    private String displayName;
    private ParameterType type;

    public Parameter() {
        this(null, null);
    }

    public Parameter(final String displayName, final ParameterType type) {
        this.displayName = displayName;
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(final ParameterType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Parameter other = (Parameter) obj;

        return Objects.equals(this.displayName, other.displayName) && Objects.equals(this.type, other.type);
    }

    @Override
    public String toString() {
        return String.format("Parameter{displayName='%s', type=%s}", displayName, type);
    }
}
