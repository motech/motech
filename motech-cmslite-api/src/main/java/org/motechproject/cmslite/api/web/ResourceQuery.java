package org.motechproject.cmslite.api.web;

/**
 * Resource query parameters : language, name/tag and type. Unique identification for CMS Content
 */
public class ResourceQuery {
    private String language;
    private String name;
    private String type;

    public ResourceQuery(String language, String name, String type) {
        this.language = language;
        this.name = name;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
