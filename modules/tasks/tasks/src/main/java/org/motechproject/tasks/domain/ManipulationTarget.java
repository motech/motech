package org.motechproject.tasks.domain;

import org.codehaus.jackson.annotate.JsonValue;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Defines the target of various manipulations used in a task for both triggers and data sources.
 */

public enum ManipulationTarget {
    STRING("string"),
    DATE("date"),
    ALL("all");

    private final String value;

    public Object parse(String value) {
        return value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    private ManipulationTarget(String value) {
        this.value = value;
    }

    public static ManipulationTarget fromString(String string) {
        ManipulationTarget result = null;

        if (isNotBlank(string)) {
            for (ManipulationTarget level : ManipulationTarget.values()) {
                if (level.getValue().equalsIgnoreCase(string)) {
                    result = level;
                    break;
                }
            }
        }

        return result;
    }
}
