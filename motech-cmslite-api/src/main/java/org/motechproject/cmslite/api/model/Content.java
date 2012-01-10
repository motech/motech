package org.motechproject.cmslite.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.model.MotechBaseDataObject;

public abstract class Content extends MotechBaseDataObject {
    @JsonProperty
    private String language;
    @JsonProperty
    private String name;

    protected Content() {
    }

    protected Content(String language, String name) {
        this.name = name;
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }
}