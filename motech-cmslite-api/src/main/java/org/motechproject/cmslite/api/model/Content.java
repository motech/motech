package org.motechproject.cmslite.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'CONTENT'")
public class Content extends MotechAuditableDataObject {
    @JsonProperty
    private String language;
    @JsonProperty
    private String name;

    public Content() {
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