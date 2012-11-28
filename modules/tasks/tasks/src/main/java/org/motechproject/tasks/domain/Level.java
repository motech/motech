package org.motechproject.tasks.domain;

import org.codehaus.jackson.annotate.JsonValue;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public enum Level {
    ERROR("ERROR"),
    WARNING("WARNING"),
    SUCCESS("SUCCESS");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    private Level(String value) {
        this.value = value;
    }

    public static Level fromString(String string) {
        Level result = null;

        if (isNotBlank(string)) {
            for (Level level : Level.values()) {
                if (level.getValue().equalsIgnoreCase(string)) {
                    result = level;
                    break;
                }
            }
        }

        return result;
    }
}
