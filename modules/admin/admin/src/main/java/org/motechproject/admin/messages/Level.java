package org.motechproject.admin.messages;

import org.codehaus.jackson.annotate.JsonValue;

public enum Level {
    INFO("INFO"), ERROR("ERROR"), WARN("WARN"), DEBUG("DEBUG"), OK("OK");

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
        if (string != null) {
            for (Level level : Level.values()) {
                if (level.getValue().equals(string.toUpperCase())) {
                    result = level;
                    break;
                }
            }
        }
        return result;
    }
}
