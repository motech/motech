package org.motechproject.tasks.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonValue;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.motechproject.commons.api.MotechException;

import java.util.Arrays;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Defines the type of various values used in a task including trigger parameters, action parameters and data source object fields.
 */
public enum ParameterType {
    UNICODE("UNICODE") {
        @Override
        public Object parse(String value) {
            return value;
        }
    },

    TEXTAREA("TEXTAREA") {
        @Override
        public Object parse(String value) {
            return value;
        }
    },

    INTEGER("INTEGER") {
        @Override
        public Object parse(String value) {
            try {
                return Integer.valueOf(value);
            } catch (Exception e) {
                throw new MotechException("task.error.convertToInteger", e);
            }
        }
    },

    LONG("LONG") {
        @Override
        public Object parse(String value) {
            try {
                return Long.valueOf(value);
            } catch (Exception e) {
                throw new MotechException("task.error.convertToLong", e);
            }
        }
    },

    DOUBLE("DOUBLE") {
        @Override
        public Object parse(String value) {
            try {
                return Double.valueOf(value);
            } catch (Exception e) {
                throw new MotechException("task.error.convertToDouble", e);
            }
        }
    },

    DATE("DATE") {
        @Override
        public Object parse(String value) {
            try {
                DateTimeParser[] parsers = {
                        DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").getParser(),
                        DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z").getParser(),
                        DateTimeFormat.forPattern("yyyy-MM-dd").getParser(),
                        DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss ZZZ yyyy").getParser()
                };

                DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();
                return formatter.parseDateTime(value);
            } catch (Exception e) {
                throw new MotechException("task.error.convertToDate", e);
            }
        }
    },

    TIME("TIME") {
        @Override
        public Object parse(String value) {
            try {
                DateTimeParser[] parsers = {
                        DateTimeFormat.forPattern("HH:mm Z").getParser(),
                        DateTimeFormat.forPattern("HH:mm").getParser()
                };

                DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();
                return formatter.parseDateTime(value);
            } catch (Exception e) {
                throw new MotechException("task.error.convertToTime", e);
            }
        }
    },

    PERIOD("PERIOD") {
        @Override
        public Object parse(String value) {
            try {
                return Period.parse(value);
            } catch (Exception e) {
                throw new MotechException("task.error.convertToPeriod", e);
            }
        }
    },

    BOOLEAN("BOOLEAN") {
        @Override
        public Object parse(String value) {
            if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                throw new MotechException("task.error.convertToBoolean");
            }
            return Boolean.valueOf(value);
        }
    },

    LIST("LIST") {
        @Override
        public Object parse(String value) {
            return Arrays.asList(value.split("(\\r)?\\n"));
        }
    },

    MAP("MAP") {
        @Override
        public Object parse(String value) {
            throw new UnsupportedOperationException("Map convert not supported.");
        }
    },

    UNKNOWN("UNKNOWN") {
        @Override
        public Object parse(String value) {
            return value;
        }
    };

    private final String value;

    public abstract Object parse(String value);

    public static ParameterType getType(Class clazz) {
        ParameterType type = getNumericalType(clazz);
        if (clazz.equals(Boolean.class) || clazz.equals(Boolean.TYPE)) {
            type = BOOLEAN;
        } else if (clazz.equals(DateTime.class)) {
            type = DATE;
        } else if (clazz.equals(Period.class)) {
            type = PERIOD;
        } else if (type == null) {
            type = UNICODE;
        }
        return type;
    }

    private static ParameterType getNumericalType(Class clazz) {
        if (clazz.equals(Double.class) || clazz.equals(Double.TYPE)) {
            return DOUBLE;
        } else if (clazz.equals(Integer.class) || clazz.equals(Integer.TYPE)) {
            return INTEGER;
        } else if (clazz.equals(Long.class) || clazz.equals(Long.TYPE)) {
            return LONG;
        }  else {
            return null;
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
