package org.motechproject.couch.mrs.model;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.mrs.domain.Attribute;

/**
 * Used for storing user attributes in property => value format
 */

public class CouchAttribute implements Attribute {

    private static final long serialVersionUID = -220219868470497301L;

    @JsonProperty
    private String name;

    @JsonProperty
    private String value;

    /**
     * Creates an attribute with the given name (property) and value
     * @param name Property of the information
     * @param value Value for the property
     */
    public CouchAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public CouchAttribute() {
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attribute)) {
            return false;
        }
        Attribute a = (Attribute) o;
        if (!ObjectUtils.equals(name, a.getName())) {
            return false;
        }
        if (!ObjectUtils.equals(value, a.getValue())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + ObjectUtils.hashCode(name);
        hash = hash * 31 + ObjectUtils.hashCode(value);
        return hash;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
