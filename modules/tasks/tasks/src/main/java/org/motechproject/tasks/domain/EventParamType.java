package org.motechproject.tasks.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonValue;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public enum EventParamType {
    UNICODE("UNICODE"),
    TEXTAREA("TEXTAREA"),
    NUMBER("NUMBER");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonIgnore
    public boolean isString() {
        return value.equalsIgnoreCase(UNICODE.getValue()) || value.equalsIgnoreCase(TEXTAREA.getValue());
    }

    @JsonIgnore
    public boolean isNumber() {
        return value.equalsIgnoreCase(NUMBER.getValue());
    }

    private EventParamType(String value) {
        this.value = value;
    }

    public static EventParamType fromString(String string) {
        EventParamType result = null;

        if (isNotBlank(string)) {
            for (EventParamType level : EventParamType.values()) {
                if (level.getValue().equalsIgnoreCase(string)) {
                    result = level;
                    break;
                }
            }
        }

        return result;
    }
}
