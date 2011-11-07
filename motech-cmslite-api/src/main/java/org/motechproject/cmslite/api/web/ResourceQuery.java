package org.motechproject.cmslite.api.web;

public class ResourceQuery {
    private String language;
    private String name;

    public ResourceQuery(String language, String name) {
        this.language = language;
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
