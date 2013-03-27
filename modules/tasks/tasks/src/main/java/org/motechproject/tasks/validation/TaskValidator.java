package org.motechproject.tasks.validation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskAdditionalData;
import org.motechproject.tasks.domain.TaskEventInformation;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TaskValidator extends GeneralValidator {
    public static final String TASK = "task";

    private static final String MODULE_VERSION = "moduleVersion";

    private TaskValidator() {
    }

    public static ValidationResult validate(Task task) {
        ValidationResult result = new ValidationResult();

        result.addError(checkBlankValue(TASK, "name", task.getName()));

        result.addErrors(validateTrigger(task.getTrigger()));
        result.addErrors(validateAction(task.getAction()));

        result.addError(checkNullValue(TASK, "actionInputFields", task.getActionInputFields()));
        if (task.getActionInputFields() != null) {
            result.addErrors(validateDateFormat(task.getActionInputFields()));
        }

        if (task.getFilters() != null) {
            for (int i = 0; i < task.getFilters().size(); ++i) {
                result.addErrors(validateFilter(i, task.getFilters().get(i)));
            }
        }

        if (task.getAdditionalData() != null) {
            for (Map.Entry<String, List<TaskAdditionalData>> entry : task.getAdditionalData().entrySet()) {
                for (int i = 0; i < entry.getValue().size(); ++i) {
                    result.addErrors(validateAdditionalData(entry.getKey(), i, entry.getValue().get(i)));
                }
            }
        }

        return result;
    }

    private static ValidationResult validateDateFormat(Map<String, String> actionInputFields) {
        ValidationResult result = new ValidationResult();

        for (Map.Entry<String, String> entry : actionInputFields.entrySet()) {
            String entryValue = entry.getValue();
            if (entryValue.contains("dateTime")) {
                Pattern pattern = Pattern.compile("\\{\\{(.*?)\\?dateTime\\((.*?)\\)\\}\\}");
                Matcher matcher = pattern.matcher(entryValue);
                while (matcher.find()) {
                    try {
                        DateTime now = DateTime.now();
                        DateTimeFormat.forPattern(matcher.group(2)).print(now);
                    } catch (IllegalArgumentException e) {
                        String[] objectFields = matcher.group(1).split("\\.");
                        result.addError(new CustomTaskError(String.format("%s.%s", objectFields[objectFields.length - 2], objectFields[objectFields.length - 1]), entry.getKey(), "validation.error.dateFormat"));
                    }
                }
            }
        }

        return result;
    }

    private static ValidationResult validateAction(TaskActionInformation action) {
        ValidationResult result = new ValidationResult();

        result.addError(checkNullValue(TASK, "action", action));

        if (result.isValid()) {
            String objectName = TASK + ".action";

            result.addError(checkBlankValue(objectName, "channelName", action.getChannelName()));
            result.addError(checkBlankValue(objectName, "moduleName", action.getModuleName()));
            result.addError(checkBlankValue(objectName, MODULE_VERSION, action.getModuleVersion()));

            result.addError(checkVersion(objectName, MODULE_VERSION, action.getModuleVersion()));

            if (!action.hasSubject() && !action.hasService()) {
                result.addError(new CustomTaskError("validation.error.taskAction"));
            }
        }

        return result;
    }

    private static ValidationResult validateTrigger(TaskEventInformation trigger) {
        ValidationResult result = new ValidationResult();

        result.addError(checkNullValue(TASK, "action", trigger));

        if (result.isValid()) {
            String objectName = TASK + ".trigger";

            result.addError(checkBlankValue(objectName, "channelName", trigger.getChannelName()));
            result.addError(checkBlankValue(objectName, "moduleName", trigger.getModuleName()));
            result.addError(checkBlankValue(objectName, MODULE_VERSION, trigger.getModuleVersion()));
            result.addError(checkBlankValue(objectName, "subject", trigger.getSubject()));

            result.addError(checkVersion(objectName, MODULE_VERSION, trigger.getModuleVersion()));
        }

        return result;
    }

    private static ValidationResult validateFilter(int index, Filter filter) {
        ValidationResult result = new ValidationResult();
        String field = "filters[" + index + "]";

        result.addError(checkNullValue(TASK, field, filter));

        if (result.isValid()) {
            String objectName = "task." + field;

            result.addError(checkBlankValue(objectName, "operator", filter.getOperator()));
            result.addError(checkBlankValue(objectName, "expression", filter.getExpression()));

            result.addErrors(validateEventParameter(objectName, "eventParameter", filter.getEventParameter()));
        }

        return result;
    }

    private static ValidationResult validateAdditionalData(String key, int index, TaskAdditionalData additionalData) {
        ValidationResult result = new ValidationResult();
        String field = "additionalData[" + key + "][" + index + "]";

        result.addError(checkNullValue(TASK, field, additionalData));

        if (result.isValid()) {
            String objectName = "task." + field;

            result.addError(checkNullValue(objectName, "id", additionalData.getId()));

            result.addError(checkBlankValue(objectName, "type", additionalData.getType()));
            result.addError(checkBlankValue(objectName, "lookupField", additionalData.getLookupField()));
            result.addError(checkBlankValue(objectName, "lookupValue", additionalData.getLookupValue()));
        }

        return result;
    }

}
