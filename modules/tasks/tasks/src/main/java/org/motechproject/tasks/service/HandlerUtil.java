package org.motechproject.tasks.service;

import org.apache.commons.lang.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.commons.api.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.domain.OperatorType;
import org.motechproject.tasks.domain.ParameterType;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskAdditionalData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

final class HandlerUtil {
    private HandlerUtil() {
    }

    public static Object convertTo(ParameterType type, String userInput) {
        Object value;

        switch (type) {
            case DOUBLE:
                value = convertToDouble(userInput);
                break;
            case INTEGER:
                value = convertToInteger(userInput);
                break;
            case LONG:
                value = convertToLong(userInput);
                break;
            case BOOLEAN:
                value = convertToBoolean(userInput);
                break;
            case TIME:
                value = convertToTime(userInput);
                break;
            case DATE:
                value = convertToDate(userInput);
                break;
            default:
                value = userInput;
        }

        return value;
    }

    public static Object getFieldValue(Object object, String key) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] fields = key.split("\\.");
        Object current = object;

        for (String field : fields) {
            if (current == null) {
                throw new IllegalStateException("Field on path is null");
            } else if (current instanceof Map) {
                current = ((Map) current).get(field);
            } else {
                Method method = current.getClass().getMethod("get" + WordUtils.capitalize(field));
                current = method.invoke(current);
            }
        }

        return current;
    }

    public static TaskAdditionalData findAdditionalData(Task task, KeyInformation key) {
        String dataProviderId = key.getDataProviderId();
        TaskAdditionalData taskAdditionalData = null;

        if (task.containsAdditionalData(dataProviderId)) {
            for (TaskAdditionalData ad : task.getAdditionalData(dataProviderId)) {
                if (ad.objectEquals(key.getObjectId(), key.getObjectType())) {
                    taskAdditionalData = ad;
                    break;
                }
            }
        }

        return taskAdditionalData;
    }

    public static String getTriggerKey(MotechEvent event, KeyInformation key) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String value = "";

        if (event.getParameters() != null) {
            value = getFieldValue(event.getParameters(), key.getKey()).toString();
        }

        return value;
    }

    public static boolean checkFilters(List<Filter> filters, Map<String, Object> triggerParameters) {
        boolean filterCheck = filters == null || filters.isEmpty();

        if (filters != null && triggerParameters != null) {
            for (Filter filter : filters) {
                EventParameter eventParameter = filter.getEventParameter();

                if (triggerParameters.containsKey(eventParameter.getEventKey())) {
                    ParameterType type = eventParameter.getType();
                    Object object = triggerParameters.get(eventParameter.getEventKey());

                    if (type.isString()) {
                        filterCheck = checkFilterForString(filter, (String) object);
                    } else if (type.isNumber()) {
                        filterCheck = checkFilterForNumber(filter, new BigDecimal(object.toString()));
                    }

                    if (!filter.isNegationOperator()) {
                        filterCheck = !filterCheck;
                    }
                }

                if (!filterCheck) {
                    break;
                }
            }
        }

        return filterCheck;
    }

    private static boolean checkFilterForString(Filter filter, String param) {
        OperatorType operatorType = OperatorType.fromString(filter.getOperator());
        String expression = filter.getExpression();
        boolean result = false;

        if (operatorType != null) {
            switch (operatorType) {
                case EQUALS:
                    result = param.equals(expression);
                    break;
                case CONTAINS:
                    result = param.contains(expression);
                    break;
                case EXIST:
                    result = true;
                    break;
                case STARTSWITH:
                    result = param.startsWith(expression);
                    break;
                case ENDSWITH:
                    result = param.endsWith(expression);
                    break;
                default:
                    result = false;
            }
        }

        return result;
    }

    private static boolean checkFilterForNumber(Filter filter, BigDecimal param) {
        OperatorType operatorType = OperatorType.fromString(filter.getOperator());
        boolean result = false;
        int compare;

        if (operatorType == null || operatorType == OperatorType.EXIST) {
            compare = 0;
        } else {
            compare = param.compareTo(new BigDecimal(filter.getExpression()));
        }

        if (operatorType != null) {
            switch (operatorType) {
                case EQUALS:
                    result = compare == 0;
                    break;
                case GT:
                    result = compare == 1;
                    break;
                case LT:
                    result = compare == -1;
                    break;
                case EXIST:
                    result = true;
                    break;
                default:
                    result = false;
            }
        }

        return result;
    }

    private static Object convertToDate(String userInput) {
        Object value;
        try {
            value = DateTime.parse(userInput, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z"));
        } catch (Exception e) {
            throw new MotechException("error.convertToDate", e);
        }
        return value;
    }

    private static Object convertToTime(String userInput) {
        Object value;
        try {
            value = DateTime.parse(userInput, DateTimeFormat.forPattern("HH:mm Z"));
        } catch (Exception e) {
            throw new MotechException("error.convertToTime", e);
        }
        return value;
    }

    private static Object convertToBoolean(String userInput) {
        if (!equalsIgnoreCase(userInput, "true") && !equalsIgnoreCase(userInput, "false")) {
            throw new MotechException("error.convertToBoolean");
        }

        return Boolean.valueOf(userInput);
    }

    private static Object convertToLong(String userInput) {
        Object value;
        try {
            value = Long.valueOf(userInput);
        } catch (Exception e) {
            throw new MotechException("error.convertToLong", e);
        }
        return value;
    }

    private static Object convertToInteger(String userInput) {
        Object value;
        try {
            value = Integer.valueOf(userInput);
        } catch (Exception e) {
            throw new MotechException("error.convertToInteger", e);
        }
        return value;
    }

    private static Object convertToDouble(String userInput) {
        Object value;
        try {
            value = Double.valueOf(userInput);
        } catch (Exception e) {
            throw new MotechException("error.convertToDouble", e);
        }
        return value;
    }
}
