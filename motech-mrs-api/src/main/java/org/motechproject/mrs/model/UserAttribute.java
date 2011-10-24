package org.motechproject.mrs.model;

public class UserAttribute {
    private String name;
    private String value;

    public UserAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }
}
