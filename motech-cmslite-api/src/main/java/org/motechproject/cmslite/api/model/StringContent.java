package org.motechproject.cmslite.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type === 'STRING_CONTENT'")
public class StringContent extends Content {
    @JsonProperty
    private String value;
    @JsonProperty
    private String type = "STRING_CONTENT";

    public StringContent() {
    }

    public StringContent(String language, String name, String value) {
        super(language, name);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
