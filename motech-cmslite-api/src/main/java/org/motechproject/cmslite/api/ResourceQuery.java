package org.motechproject.cmslite.api;

public class ResourceQuery {

    private final String language;

    private final String name;

    public ResourceQuery(String name, String language){
        this.name = name;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }
}
