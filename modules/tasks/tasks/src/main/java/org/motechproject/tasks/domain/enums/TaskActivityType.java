package org.motechproject.tasks.domain.enums;

import org.codehaus.jackson.annotate.JsonValue;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Enumerates all types of task activities.
 */
public enum TaskActivityType {

    ERROR("ERROR"),
    WARNING("WARNING"),
    SUCCESS("SUCCESS"),
    IN_PROGRESS("IN PROGRESS"),
    FILTERED("FILTERED");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    TaskActivityType(String value) {
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
