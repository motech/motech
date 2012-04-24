/**
 * \ingroup MRS
 * Domain classes for MRS
 */
package org.motechproject.mrs.model;

/**
 * Used for storing user attributes in property => value format
 */
public class Attribute {
    private String name;
    private String value;

    /**
     * Creates an attribute with the given name (property) and value
     *
     * @param name  Property of the information
     * @param value Value for the property
     */
    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }
}
