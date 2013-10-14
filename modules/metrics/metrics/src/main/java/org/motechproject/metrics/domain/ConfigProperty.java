package org.motechproject.metrics.domain;

/**
 * Class for keeping info about metrics backend config property
 */
public class ConfigProperty {

    private String displayName;
    private PropertyType type;
    private String value;

    public ConfigProperty(String displayName, PropertyType type, String value) {
        this.displayName = displayName;
        this.type = type;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public PropertyType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
