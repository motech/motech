package org.motechproject.cmslite.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type === 'STRING_CONTENT'")
public class StringContent extends Content {
    @JsonProperty
    private String value;

    public StringContent(String language, String name, String value) {
        super(language, name);
        this.value = value;
    }
}
