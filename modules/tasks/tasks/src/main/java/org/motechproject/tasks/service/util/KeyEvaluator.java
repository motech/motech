package org.motechproject.tasks.service.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.api.MotechException;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.exception.TaskHandlerException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.tasks.domain.KeyInformation.ADDITIONAL_DATA_PREFIX;
import static org.motechproject.tasks.domain.KeyInformation.POST_ACTION_PARAMETER_PREFIX;
import static org.motechproject.tasks.domain.KeyInformation.TRIGGER_PREFIX;
import static org.motechproject.tasks.domain.KeyInformation.parseAll;
import static org.motechproject.tasks.constants.TaskFailureCause.POST_ACTION_PARAMETER;
import static org.motechproject.tasks.constants.TaskFailureCause.TRIGGER;

/**
 * KeyEvaluator evaluates the value of a key in the context of a task which is used to execute filters and actions.
 */
public class KeyEvaluator {

    private static final int JOIN_PATTERN_BEGIN_INDEX = 5;
    private static final int DATETIME_PATTERN_BEGIN_INDEX = 9;
    private static final int FORMAT_PATTERN_BEGIN_INDEX = 7;
    private static final int SUBSTRING_PATTERN_BEGIN_INDEX = 10;
    private static final int SPLIT_PATTERN_BEGIN_INDEX = 6;
    private static final int PLUS_MONTHS_PATTERN_BEGIN_INDEX = 11;
    private static final int MINUS_MONTHS_PATTERN_BEGIN_INDEX = 12;
    private static final int PLUS_DAYS_PATTERN_BEGIN_INDEX = 9;
    private static final int MINUS_DAYS_PATTERN_BEGIN_INDEX = 10;
    private static final int PLUS_HOURS_PATTERN_BEGIN_INDEX = 10;
    private static final int MINUS_HOURS_PATTERN_BEGIN_INDEX = 11;
    private static final int PLUS_MINUTES_PATTERN_BEGIN_INDEX = 12;
    private static final int MINUS_MINUTES_PATTERN_BEGIN_INDEX = 13;
    private static final int PARSE_DATE_PATTERN_BEGIN_INDEX = 10;

    private TaskContext taskContext;

    /**
     * Class constructor.
     *
     * @param taskContext  the task context, not null
     */
    public KeyEvaluator(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    /**
     * Evaluates the given template by replacing the keys with their manipulated values.
     *
     * @param template  the template to be evaluated
     * @return the evaluated template
     * @throws TaskHandlerException if there was problem while manipulating the value
     */
    public String evaluateTemplateString(String template) throws TaskHandlerException {
        String conversionTemplate = template;

        for (KeyInformation key : parseAll(template)) {
            Object value = getValue(key);
            String stringValue = value != null ? value.toString() : "";

            stringValue = manipulateValue(key.getManipulations(), stringValue);

            conversionTemplate = conversionTemplate.replace(
                    String.format("{{%s}}", key.getOriginalKey()), stringValue
            );
        }

        return conversionTemplate;
    }

    /**
     * Returns value for the given key.
     *
     * @param keyInformation  the key information, not null
     * @return the value for the given key
     * @throws TaskHandlerException if there were problems while retrieving the value
     */
    public Object getValue(KeyInformation keyInformation) throws TaskHandlerException {
        Object value = null;

        switch (keyInformation.getPrefix()) {
            case TRIGGER_PREFIX:
                try {
                    value = taskContext.getTriggerValue(keyInformation.getKey());
                } catch (RuntimeException e) {
                    throw new TaskHandlerException(
                            TRIGGER, "task.error.objectDoesNotContainField", e, keyInformation.getKey()
                    );
                }
                break;
            case ADDITIONAL_DATA_PREFIX:
                value = taskContext.getDataSourceObjectValue(keyInformation.getObjectId().toString(), keyInformation.getKey(), keyInformation.getObjectType());
                break;
            case POST_ACTION_PARAMETER_PREFIX:
                try {
                    value = taskContext.getPostActionParameterValue(keyInformation.getObjectId().toString(), keyInformation.getKey());
                } catch (RuntimeException e) {
                    throw new TaskHandlerException(
                            POST_ACTION_PARAMETER, "task.error.objectDoesNotContainField", e, keyInformation.getKey()
                    );
                }
                break;
            default:
        }

        return value;
    }

    /**
     * Retrieves the value for the given key and applies all the passed manipulations.
     *
     * @param keyInformation  the key information, not null
     * @return the manipulated value
     * @throws TaskHandlerException if there were problems while retrieving the value
     */
    public Object getManipulatedValue(KeyInformation keyInformation) throws TaskHandlerException {
        Object value = getValue(keyInformation);
        List<String> manipulations = keyInformation.getManipulations();
        if (manipulations.size() > 0) {
            String stringValue = value != null ? value.toString() : "";
            value = manipulateValue(manipulations, stringValue);
        }
        return value;
    }

    private String manipulateValue(List<String> manipulations, String value) throws TaskHandlerException {
        String manipulateValue = value;
        for (String manipulation : manipulations) {
            if (manipulation.contains("format")) {
                String formatElements = manipulation.substring(FORMAT_PATTERN_BEGIN_INDEX, manipulation.length() - 1);

                if (isNotBlank(formatElements)) {
                    String[] items = formatElements.split(",");

                    for (int i = 0; i < items.length; ++i) {
                        String item = items[i];

                        if (item.startsWith("{{") && item.endsWith("}}")) {
                            item = item.substring(2, item.length() - 2);
                            KeyInformation subKey = KeyInformation.parse(item);
                            Object subValue = getValue(subKey);
                            items[i] = subValue != null ? subValue.toString() : "";
                        }
                    }

                    manipulateValue = String.format(manipulateValue, items);
                }
            } else {
                try {
                    manipulateValue = manipulate(manipulation, manipulateValue);
                } catch (MotechException e) {
                    String msg = e.getMessage();

                    if ("task.warning.manipulation".equalsIgnoreCase(msg)) {
                        taskContext.publishWarningActivity(msg, manipulation);
                    } else {
                        throw new TaskHandlerException(TRIGGER, msg, e, manipulation);
                    }
                }
            }
        }
        return manipulateValue;
    }

    String manipulate(String manipulation, String value) {
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
        } else if (lowerCase.contains("parsedate")) {
            result = parseDate(value, manipulation);
        } else if (lowerCase.contains("plus") || lowerCase.contains("minus") || lowerCase.contains("ofmonth")) {
            result = dateTimeChangeManipulation(value, lowerCase);
        } else {
            result = simpleManipulations(value, lowerCase.replace("()", ""));
        }

        return result;
    }

    private String parseDate(String value, String manipulation) {
        String pattern = manipulation.substring(PARSE_DATE_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        DateTime dateTime = formatter.parseDateTime(value);

        return dateTime.toString("yyyy-MM-dd HH:mm Z");
    }

    private String joinManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(JOIN_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        String[] splitValue = value.split(" ");

        return StringUtils.join(splitValue, pattern);
    }

    private String datetimeManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(DATETIME_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        DateTimeFormatter targetFormat = DateTimeFormat.forPattern(pattern);

        return targetFormat.print(new DateTime(value));
    }

    private String dateTimeChangeManipulation(String value, String manipulation) {
        String result = value;

        if (manipulation.contains("month")) {
            result = monthManipulation(value, manipulation);
        }  else if (manipulation.contains("plusdays")) {
            result = plusDaysManipulation(value, manipulation);
        } else if (manipulation.contains("minusdays")) {
            result = minusDaysManipulation(value, manipulation);
        } else if (manipulation.contains("plushours")) {
            result = plusHoursManipulation(value, manipulation);
        } else if (manipulation.contains("minushours")) {
            result = minusHoursManipulation(value, manipulation);
        } else if (manipulation.contains("plusminutes")) {
            result = plusMinutesManipulation(value, manipulation);
        } else if (manipulation.contains("minusminutes")) {
            result = minusMinutesManipulation(value, manipulation);
        } else {
            throw new MotechException("task.warning.manipulation");
        }
        return result;
    }

    private String monthManipulation(String value, String manipulation) {
        String result = value;

        if (manipulation.contains("beginningofmonth")) {
            result = beginningOfMonthManipulation(value);
        } else if (manipulation.contains("endofmonth")) {
            result = endOfMonthManipulation(value);
        } else if (manipulation.contains("plusmonths")) {
            result = plusMonthsManipulation(value, manipulation);
        } else if (manipulation.contains("minusmonths")) {
            result = minusMonthsManipulation(value, manipulation);
        }
        return result;
    }

    private String substringManipulation(String value, String manipulation) {
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

    private String splitManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(SPLIT_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        String[] splitValue = pattern.split(",");
        String regex = splitValue[0];
        int idx = Integer.parseInt(splitValue[1]);

        return value.split(regex)[idx];
    }

    private String beginningOfMonthManipulation(String value) {
        DateTime dateTime = new DateTime(value);

        return dateTime.dayOfMonth().withMinimumValue().withTime(0, 0, 0, 0).toString();
    }

    private String endOfMonthManipulation(String value) {
        DateTime dateTime = new DateTime(value);

        return dateTime.dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999).toString();
    }

    private String plusMonthsManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(PLUS_MONTHS_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        DateTime dateTime = new DateTime(value);

        return dateTime.plusMonths(Integer.parseInt(pattern)).toString();
    }

    private String minusMonthsManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(MINUS_MONTHS_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        DateTime dateTime = new DateTime(value);

        return dateTime.minusMonths(Integer.parseInt(pattern)).toString();
    }

    private String plusDaysManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(PLUS_DAYS_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        DateTime dateTime = new DateTime(value);

        return dateTime.plusDays(Integer.parseInt(pattern)).toString();
    }

    private String minusDaysManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(MINUS_DAYS_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        DateTime dateTime = new DateTime(value);

        return dateTime.minusDays(Integer.parseInt(pattern)).toString();
    }

    private String plusHoursManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(PLUS_HOURS_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        DateTime dateTime = new DateTime(value);

        return dateTime.plusHours(Integer.parseInt(pattern)).toString();
    }

    private String minusHoursManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(MINUS_HOURS_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        DateTime dateTime = new DateTime(value);

        return dateTime.minusHours(Integer.parseInt(pattern)).toString();
    }

    private String plusMinutesManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(PLUS_MINUTES_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        DateTime dateTime = new DateTime(value);

        return dateTime.plusMinutes(Integer.parseInt(pattern)).toString();
    }

    private String minusMinutesManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(MINUS_MINUTES_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        DateTime dateTime = new DateTime(value);

        return dateTime.minusMinutes(Integer.parseInt(pattern)).toString();
    }

    private String simpleManipulations(String value, String manipulation) {
        String result;
        switch (manipulation) {
            case "toupper":
                result = value.toUpperCase();
                break;
            case "tolower":
                result = value.toLowerCase();
                break;
            case "capitalize":
                result = WordUtils.capitalize(value);
                break;
            case "urlencode":
                try {
                    result = URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new MotechException("URLEncode manipulator error.", e);
                }
                break;
            default:
                throw new MotechException("task.warning.manipulation");
        }
        return result;
    }
}
