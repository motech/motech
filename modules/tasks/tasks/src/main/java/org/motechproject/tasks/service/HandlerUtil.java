package org.motechproject.tasks.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.domain.OperatorType;
import org.motechproject.tasks.domain.ParameterType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.motechproject.tasks.domain.KeyInformation.ADDITIONAL_DATA_PREFIX;
import static org.motechproject.tasks.domain.KeyInformation.TRIGGER_PREFIX;
import static org.motechproject.tasks.domain.KeyInformation.parse;

/**
 * The <code>HandlerUtil</code> class provides utility methods for {@link TaskTriggerHandler} class.
 * <p/>
 * <ul>
 * <li><b>convertTo</b> - convert a given value to a correct type,</li>
 * <li><b>getFieldValue</b> - get value of a field defined in the key from the given object,</li>
 * <li><b>getTriggerKey</b> - get value of a trigger event parameter,</li>
 * <li><b>checkFilters</b> - executed defined filters for a task,</li>
 * <li><b>manipulate</b> - executed the given manipulation on the given string value.</li>
 * </ul>
 * <p/>
 * The <code>HandlerUtil</code> class defines certain indexes related to manipulation.
 * <p/>
 * <ul>
 * <li><b>JOIN_PATTERN_BEGIN_INDEX</b> - index of opening bracket in join manipulation,</li>
 * <li><b>DATETIME_PATTERN_BEGIN_INDEX</b> - index of opening bracket in date manipulation,</li>
 * <li><b>FORMAT_PATTERN_BEGIN_INDEX</b> - index of opening bracket in format manipulation.</li>
 * <li><b>SUBSTRING_PATTERN_BEGIN_INDEX</b> - index of opening bracket in substring manipulation.</li>
 * <li><b>SPLIT_PATTERN_BEGIN_INDEX</b> - index of opening bracket in split manipulation.</li>
 * </ul>
 *
 * @see {@link TaskTriggerHandler}
 * @since 0.19
 */
final class HandlerUtil {
    public static final int JOIN_PATTERN_BEGIN_INDEX = 5;
    public static final int DATETIME_PATTERN_BEGIN_INDEX = 9;
    public static final int FORMAT_PATTERN_BEGIN_INDEX = 7;
    public static final int SUBSTRING_PATTERN_BEGIN_INDEX = 10;
    public static final int SPLIT_PATTERN_BEGIN_INDEX = 6;

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

    public static Object getFieldValue(Object object, String key) {
        String[] fields = key.split("\\.");
        Object current = object;

        for (String field : fields) {
            if (current == null) {
                throw new IllegalStateException("Field on path is null");
            } else if (current instanceof Map) {
                current = ((Map) current).get(field);
            } else {
                try {
                    Method method = current.getClass().getMethod("get" + WordUtils.capitalize(field));
                    current = method.invoke(current);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new MotechException(e.getMessage(), e);
                }
            }
        }

        return current;
    }

    public static Object getTriggerKey(MotechEvent event, KeyInformation key) {
        Object value = null;

        if (event.getParameters() != null) {
            value = getFieldValue(event.getParameters(), key.getKey());
        }

        return value;
    }

    public static boolean checkFilters(List<Filter> filters, Map<String, Object> parameters,
                                       Map<String, Object> dataSourceObjects) {
        boolean filterCheck = filters == null || filters.isEmpty();

        if (filters != null && parameters != null) {
            for (Filter filter : filters) {
                KeyInformation key = parse(filter.getKey());
                Object value = getFilterValue(key, parameters, dataSourceObjects);
                filterCheck = value != null && checkValue(filter, value);

                if (!filter.isNegationOperator()) {
                    filterCheck = !filterCheck;
                }

                if (!filterCheck) {
                    break;
                }
            }
        }

        return filterCheck;
    }

    public static String manipulate(String manipulation, String value) {
        String lowerCase = manipulation.toLowerCase();
        String result = value;

        if (lowerCase.contains("join")) {
            result = joinManipulation(value, manipulation);
        } else if (lowerCase.contains("datetime")) {
            try {
                result = datetimeManipulation(value, manipulation);
            } catch (IllegalArgumentException e) {
                throw new MotechException("error.date.format", e);
            }
        } else if (lowerCase.contains("substring")) {
            result = substringManipulation(value, manipulation);
        } else if (lowerCase.contains("split")) {
            result = splitManipulation(value, manipulation);
        } else {
            switch (lowerCase) {
                case "toupper":
                    result = result.toUpperCase();
                    break;
                case "tolower":
                    result = result.toLowerCase();
                    break;
                case "capitalize":
                    result = WordUtils.capitalize(result);
                    break;
                default:
                    throw new MotechException("task.warning.manipulation");
            }
        }

        return result;
    }

    private static Object getFilterValue(KeyInformation key, Map<String, Object> parameters,
                                         Map<String, Object> dataSourceObjects) {
        Object value = null;

        switch (key.getPrefix()) {
            case TRIGGER_PREFIX:
                if (parameters.containsKey(key.getKey())) {
                    value = parameters.get(key.getKey());
                }
                break;
            case ADDITIONAL_DATA_PREFIX:
                if (dataSourceObjects.containsKey(key.getObjectId().toString())) {
                    Object object = dataSourceObjects.get(key.getObjectId().toString());
                    value = getFieldValue(object, key.getKey());
                }
                break;
            default:
        }

        return value;
    }

    private static boolean checkValue(Filter filter, Object value) {
        ParameterType type = filter.getType();
        boolean filterCheck;

        if (type.isString()) {
            filterCheck = checkFilterForString(filter, value.toString());
        } else if (type.isNumber()) {
            filterCheck = checkFilterForNumber(filter, new BigDecimal(value.toString()));
        } else if (type == ParameterType.DATE) {
            filterCheck = checkFilterForDate(filter, DateTime.parse(value.toString()));
        } else {
            filterCheck = false;
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

    private static boolean checkFilterForDate(Filter filter, DateTime param) {
        OperatorType operatorType = OperatorType.fromString(filter.getOperator());
        String expression = filter.getExpression();
        boolean result = false;

        if (operatorType != null) {
            switch (operatorType) {
                case EXIST:
                    result = true;
                    break;
                case EQUALS:
                    result = param.isEqual(DateTime.parse(expression));
                    break;
                case AFTER:
                    result = param.isAfter(DateTime.parse(expression));
                    break;
                case AFTER_NOW:
                    result = param.isAfterNow();
                    break;
                case BEFORE:
                    result = param.isBefore(DateTime.parse(expression));
                    break;
                case BEFORE_NOW:
                    result = param.isBeforeNow();
                    break;
                case LESS_DAYS_FROM_NOW:
                    result = countNumberOfDays(param) < Integer.valueOf(expression);
                    break;
                case MORE_DAYS_FROM_NOW:
                    result = countNumberOfDays(param) > Integer.valueOf(expression);
                    break;
                default:
                    result = false;
            }
        }

        return result;
    }

    private static int countNumberOfDays(DateTime param) {
        return param.isBeforeNow()
                ? Days.daysBetween(param, DateUtil.now()).getDays()
                : Days.daysBetween(DateUtil.now(), param).getDays();
    }

    private static Object convertToDate(String userInput) {
        Object value;
        try {
            value = DateTime.parse(userInput, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z"));
        } catch (Exception e) {
            throw new MotechException("task.error.convertToDate", e);
        }
        return value;
    }

    private static Object convertToTime(String userInput) {
        Object value;
        try {
            value = DateTime.parse(userInput, DateTimeFormat.forPattern("HH:mm Z"));
        } catch (Exception e) {
            throw new MotechException("task.error.convertToTime", e);
        }
        return value;
    }

    private static Object convertToBoolean(String userInput) {
        if (!"true".equalsIgnoreCase(userInput) && !"false".equalsIgnoreCase(userInput)) {
            throw new MotechException("task.error.convertToBoolean");
        }

        return Boolean.valueOf(userInput);
    }

    private static Object convertToLong(String userInput) {
        Object value;
        try {
            value = Long.valueOf(userInput);
        } catch (Exception e) {
            throw new MotechException("task.error.convertToLong", e);
        }
        return value;
    }

    private static Object convertToInteger(String userInput) {
        Object value;
        try {
            value = Integer.valueOf(userInput);
        } catch (Exception e) {
            throw new MotechException("task.error.convertToInteger", e);
        }
        return value;
    }

    private static Object convertToDouble(String userInput) {
        Object value;
        try {
            value = Double.valueOf(userInput);
        } catch (Exception e) {
            throw new MotechException("task.error.convertToDouble", e);
        }
        return value;
    }

    private static String joinManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(JOIN_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        String[] splitValue = value.split(" ");

        return StringUtils.join(splitValue, pattern);
    }

    private static String datetimeManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(DATETIME_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        DateTimeFormatter format = DateTimeFormat.forPattern(pattern);

        return format.print(new DateTime(value));
    }

    private static String substringManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(SUBSTRING_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        String[] splitValue = pattern.contains(",") ? pattern.split(",") : new String[]{pattern};
        int[] indexes = new int[splitValue.length];

        for (int i = 0; i < splitValue.length; ++i) {
            indexes[i] = Integer.parseInt(splitValue[i]);
        }

        switch (indexes.length) {
            case 1:
                return value.substring(indexes[0]);
            case 2:
                return value.substring(indexes[0], indexes[1]);
            default:
                throw new IllegalArgumentException("Incorrect pattern for substring manipulation");
        }
    }

    private static String splitManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(SPLIT_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        String[] splitValue = pattern.split(",");
        String regex = splitValue[0];
        int idx = Integer.parseInt(splitValue[1]);

        return value.split(regex)[idx];
    }
}
