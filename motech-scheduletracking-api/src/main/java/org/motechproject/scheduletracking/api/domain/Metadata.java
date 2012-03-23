package org.motechproject.scheduletracking.api.domain;

public class Metadata {
    String property;
    String value;

    public Metadata() {
    }

    public Metadata(String property, String value) {
        this.property = property;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metadata metadata = (Metadata) o;

        if (property != null ? !property.equals(metadata.property) : metadata.property != null) return false;
        if (value != null ? !value.equals(metadata.value) : metadata.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = property != null ? property.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
