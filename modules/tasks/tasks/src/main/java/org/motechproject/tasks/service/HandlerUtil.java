package org.motechproject.tasks.service;

import org.apache.commons.lang.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.OperatorType;
import org.motechproject.tasks.domain.ParameterType;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskAdditionalData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;

final class HandlerUtil {
    public static final String TRIGGER_PREFIX = "trigger";
    public static final String ADDITIONAL_DATA_PREFIX = "ad";

    private HandlerUtil() {
    }

    public static String getFieldValue(Object object, String key) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] fields = key.split("\\.");
        Object current = object;

        for (String f : fields) {
            Method method = current.getClass().getMethod("get" + WordUtils.capitalize(f));
            current = method.invoke(current);
        }

        return current.toString();
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

    public static String getTriggerKey(MotechEvent event, KeyInformation key) {
        String value = "";

        if (event.getParameters() != null && event.getParameters().containsKey(key.getEventKey())) {
            Object obj = event.getParameters().get(key.getEventKey());

            if (obj == null) {
                obj = "";
            }

            value = String.valueOf(obj);
        }

        return value;
    }

    public static List<KeyInformation> getKeys(String input) {
        List<KeyInformation> keys = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(isEmpty(input) ? "" : input);

        while (matcher.find()) {
            keys.add(new KeyInformation(matcher.group(1)));
        }

        return keys;
    }

    public static Object convertToNumber(String number) {
        Object value;
        BigDecimal decimal = new BigDecimal(number);

        if (decimal.signum() == 0 || decimal.scale() <= 0 || decimal.stripTrailingZeros().scale() <= 0) {
            value = decimal.intValueExact();
        } else {
            value = decimal.doubleValue();
        }

        return value;
    }

    public static DateTime convertToDate(String date) {
        return DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z"));
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
}
