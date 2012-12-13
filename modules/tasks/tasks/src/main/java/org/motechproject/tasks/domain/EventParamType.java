package org.motechproject.tasks.domain;

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
