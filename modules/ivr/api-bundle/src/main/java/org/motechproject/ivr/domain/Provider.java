package org.motechproject.ivr.domain;

public final class Provider {

    private String name;

    public Provider(){}

    public Provider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
