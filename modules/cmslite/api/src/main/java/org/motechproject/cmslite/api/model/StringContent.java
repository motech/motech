package org.motechproject.cmslite.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.Map;

/**
 * \ingroup cmslite
 * Represents Text Content.
 */
@TypeDiscriminator("doc.type === 'StringContent'")
public class StringContent extends Content {
    @JsonProperty
    private String value;

    public StringContent() {
    }

    public StringContent(String language, String name, String value) {
        super(language, name);
        this.value = value;
    }
    
    public StringContent(String language, String name, String value, Map<String, String> metadata) {
        super(language, name, metadata);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
