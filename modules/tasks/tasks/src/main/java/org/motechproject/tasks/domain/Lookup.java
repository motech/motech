package org.motechproject.tasks.domain;

import org.motechproject.mds.annotations.Entity;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Lookup implements Serializable {
    private static final long serialVersionUID = -3560581906854128062L;

    private String field;
    private String value;

    public Lookup() {
        this(null, null);
    }

    public Lookup(String field, String value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Lookup other = (Lookup) obj;

        return Objects.equals(this.field, other.field)
                && Objects.equals(this.value, other.value);
    }
}
