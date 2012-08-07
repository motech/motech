package org.motechproject.cmslite.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.model.MotechBaseDataObject;

import java.util.Map;

/**
 * Abstract representation of CMS Lite content. Identified by name and language.
 */
public abstract class Content extends MotechBaseDataObject {
    @JsonProperty
    private String language;
    @JsonProperty
    private String name;
    @JsonProperty
    private Map<String, String> metadata;

    protected Content() {
    }

    protected Content(String language, String name) {
        this.name = name;
        this.language = language;
    }

    protected Content(String language, String name, Map<String, String> metadata) {
        this(language, name);
        this.metadata = metadata;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public String getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }
}
