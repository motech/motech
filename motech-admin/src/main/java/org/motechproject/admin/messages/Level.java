package org.motechproject.admin.messages;

import org.codehaus.jackson.annotate.JsonValue;

public enum Level {
    INFO("INFO"), ERROR("ERROR"), WARN("WARN"), DEBUG("DEBUG");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    private Level(String value) {
        this.value = value;
    }
}
