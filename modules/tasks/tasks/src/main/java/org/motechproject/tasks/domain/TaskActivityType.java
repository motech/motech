package org.motechproject.tasks.domain;

import org.codehaus.jackson.annotate.JsonValue;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public enum TaskActivityType {
    ERROR("ERROR"),
    WARNING("WARNING"),
    SUCCESS("SUCCESS");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    private TaskActivityType(String value) {
        this.value = value;
    }

    public static TaskActivityType fromString(String string) {
        TaskActivityType result = null;

        if (isNotBlank(string)) {
            for (TaskActivityType type : TaskActivityType.values()) {
                if (type.getValue().equalsIgnoreCase(string)) {
                    result = type;
                    break;
                }
            }
        }

        return result;
    }
}
