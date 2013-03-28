package org.motechproject.cmslite.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.Map;
import java.util.Objects;

/**
 * Abstract representation of CMS Lite content. Identified by name and language.
 */
public abstract class Content extends MotechBaseDataObject {
    private static final long serialVersionUID = 753195533829136573L;

    @JsonProperty
    private String language;
    @JsonProperty
    private String name;
    @JsonProperty
    private Map<String, String> metadata;

    protected Content() {
        this(null, null, null);
    }

    protected Content(String language, String name) {
        this(language, name, null);
    }

    protected Content(String language, String name, Map<String, String> metadata) {
        this.name = name;
        this.language = language;
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

    @Override
    public int hashCode() {
        return Objects.hash(language, name, metadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Content other = (Content) obj;

        return Objects.equals(this.language, other.language) &&
                Objects.equals(this.name, other.name) &&
                Objects.equals(this.metadata, other.metadata);
    }

    @Override
    public String toString() {
        return String.format("Content{language='%s', name='%s', metadata=%s}", language, name, metadata);
    }
}
