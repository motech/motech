package org.motechproject.tasks.domain;

import org.codehaus.jackson.annotate.JsonValue;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Object representation of available operators in filter definition.
 */
public enum OperatorType {
    EQUALS("task.equals"),
    CONTAINS("task.contains"),
    EXIST("task.exist"),
    STARTSWITH("task.startsWith"),
    ENDSWITH("task.endsWith"),
    GT("task.gt"),
    LT("task.lt"),
    AFTER("task.after"),
    AFTER_NOW("task.afterNow"),
    BEFORE("task.before"),
    BEFORE_NOW("task.beforeNow"),
    LESS_DAYS_FROM_NOW("task.lessDaysFromNow"),
    MORE_DAYS_FROM_NOW("task.moreDaysFromNow");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    private OperatorType(String value) {
        this.value = value;
    }

    public static OperatorType fromString(String string) {
        OperatorType result = null;

        if (isNotBlank(string)) {
            for (OperatorType level : OperatorType.values()) {
                if (level.getValue().equalsIgnoreCase(string)) {
                    result = level;
                    break;
                }
            }
        }

        return result;
    }
}
