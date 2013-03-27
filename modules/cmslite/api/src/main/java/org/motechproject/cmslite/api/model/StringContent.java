package org.motechproject.cmslite.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.Map;
import java.util.Objects;

/**
 * \ingroup cmslite
 * Represents Text Content.
 */
@TypeDiscriminator("doc.type === 'StringContent'")
public class StringContent extends Content {
    private static final long serialVersionUID = -8406650651634017618L;

    @JsonProperty
    private String value;

    public StringContent() {
        this(null, null, null);
    }

    public StringContent(String language, String name, String value) {
        this(language, name, value, null);
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

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final StringContent other = (StringContent) obj;

        return Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        return String.format("StringContent{value='%s'} %s", value, super.toString());
    }
}
