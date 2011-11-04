package org.motechproject.cmslite.api.model;

import org.motechproject.cmslite.api.model.Resource;

public class ResourceQuery {
    private final String language;
    private final String name;

    public ResourceQuery(String name, String language) {
        this.name = name;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }

    public Resource getResource() {
        Resource resource = new Resource();
        resource.setName(getName());
        resource.setLanguage(getLanguage());

        return resource;
    }
}
