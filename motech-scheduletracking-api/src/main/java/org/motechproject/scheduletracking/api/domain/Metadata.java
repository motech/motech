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
}
