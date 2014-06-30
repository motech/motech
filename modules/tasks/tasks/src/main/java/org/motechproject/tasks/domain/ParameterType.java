package org.motechproject.tasks.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonValue;
import org.joda.time.DateTime;


/**
 * Defines the type of various values used in a task including trigger parameters, action parameters and data source object fields.
 */
public enum ParameterType {
    UNICODE("UNICODE"),
    TEXTAREA("TEXTAREA"),
    INTEGER("INTEGER"),
    LONG("LONG"),
    DOUBLE("DOUBLE"),
    DATE("DATE"),
    TIME("TIME"),
    BOOLEAN("BOOLEAN"),
    LIST("LIST"),
    MAP("MAP"),
    UNKNOWN("UNKNOWN");

    private final String value;

    public Object parse(String value) {
        return "";
    }

    public static ParameterType getType(Class clazz) {

        if (clazz.equals(Double.class) || clazz.equals(Double.TYPE)) {
            return DOUBLE;
        } else if (clazz.equals(Integer.class) || clazz.equals(Integer.TYPE)) {
            return INTEGER;
        } else if (clazz.equals(Long.class) || clazz.equals(Long.TYPE)) {
            return LONG;
        } else if (clazz.equals(Boolean.class) || clazz.equals(Boolean.TYPE)) {
            return BOOLEAN;
        } else if (clazz.equals(DateTime.class)) {
            return DATE;
        } else {
            return UNICODE;
        }
    }

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
        return value.equalsIgnoreCase(INTEGER.getValue()) || value.equalsIgnoreCase(LONG.getValue()) || value.equalsIgnoreCase(DOUBLE.getValue());
    }

    private ParameterType(String value) {
        this.value = value;
    }

    public static ParameterType fromString(String string) {
        ParameterType result = null;

        if (string != null && !string.equals("")) {
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
