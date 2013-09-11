package org.motechproject.tasks.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.api.MotechException;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.ex.TaskHandlerException;

import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.tasks.domain.KeyInformation.ADDITIONAL_DATA_PREFIX;
import static org.motechproject.tasks.domain.KeyInformation.TRIGGER_PREFIX;
import static org.motechproject.tasks.domain.KeyInformation.parseAll;
import static org.motechproject.tasks.events.constants.TaskFailureCause.TRIGGER;

/**
 * KeyEvaluator evaluates the value of a key in the context of a task which is used to execute filters and actions.
 */

public class KeyEvaluator {

    private static final int JOIN_PATTERN_BEGIN_INDEX = 5;
    private static final int DATETIME_PATTERN_BEGIN_INDEX = 9;
    private static final int FORMAT_PATTERN_BEGIN_INDEX = 7;
    private static final int SUBSTRING_PATTERN_BEGIN_INDEX = 10;
    private static final int SPLIT_PATTERN_BEGIN_INDEX = 6;
    private static final int PLUS_DAYS_PATTERN_BEGIN_INDEX = 9;

    private TaskContext taskContext;

    public KeyEvaluator(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

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

    public Object getValue(KeyInformation keyInformation) throws TaskHandlerException {
        Object value = null;

        switch (keyInformation.getPrefix()) {
            case TRIGGER_PREFIX:
                try {
                    value = taskContext.getTriggerValue(keyInformation.getKey());
                } catch (Exception e) {
                    throw new TaskHandlerException(
                            TRIGGER, "task.error.objectNotContainsField", e, keyInformation.getKey()
                    );
                }
                break;
            case ADDITIONAL_DATA_PREFIX:
                value = taskContext.getDataSourceObjectValue(keyInformation.getObjectId().toString(), keyInformation.getKey(), keyInformation.getObjectType());
                break;
            default:
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
        } else if (lowerCase.contains("plusdays")) {
            result = plusDaysManipulation(value, manipulation);
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

    private String plusDaysManipulation(String value, String manipulation) {
        String pattern = manipulation.substring(PLUS_DAYS_PATTERN_BEGIN_INDEX, manipulation.length() - 1);
        DateTime dateTime = new DateTime(value);

        return dateTime.plusDays(Integer.parseInt(pattern)).toString();
    }
}
