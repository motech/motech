package org.motechproject.tasks.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonValue;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public enum ParameterType {
    UNICODE("UNICODE"),
    TEXTAREA("TEXTAREA"),
    NUMBER("NUMBER"),
    DATE("DATE");

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

    private ParameterType(String value) {
        this.value = value;
    }

    public static ParameterType fromString(String string) {
        ParameterType result = null;

        if (isNotBlank(string)) {
            for (ParameterType level : ParameterType.values()) {
                if (level.getValue().equalsIgnoreCase(string)) {
                    result = level;
                    break;
                }
            }
        }

        return result;
    }
}
