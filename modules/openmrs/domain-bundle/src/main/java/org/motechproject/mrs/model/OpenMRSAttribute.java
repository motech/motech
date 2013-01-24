/**
 * \ingroup MRS
 * Domain classes for MRS
 */
package org.motechproject.mrs.model;

import org.apache.commons.lang.ObjectUtils;
import org.motechproject.mrs.domain.Attribute;

/**
 * Used for storing user attributes in property => value format
 */
public class OpenMRSAttribute implements Attribute {

    private String name;
    private String value;

    /**
     * Creates an attribute with the given name (property) and value
     * 
     * @param name
     *            Property of the information
     * @param value
     *            Value for the property
     */
    public OpenMRSAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OpenMRSAttribute)) {
            return false;
        }
        OpenMRSAttribute a = (OpenMRSAttribute) o;
        if (!ObjectUtils.equals(name, a.name)) {
            return false;
        }
        if (!ObjectUtils.equals(value, a.value)) {
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
}
