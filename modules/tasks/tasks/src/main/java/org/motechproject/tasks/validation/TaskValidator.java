package org.motechproject.tasks.validation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskAdditionalData;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskDataProviderObject;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.domain.TaskEventInformation;
import org.motechproject.tasks.domain.TriggerEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

public final class TaskValidator extends GeneralValidator {
    public static final String TASK = "task";

    private static final String MODULE_VERSION = "moduleVersion";

    private TaskValidator() {
    }

    public static Set<TaskError> validate(Task task) {
        Set<TaskError> errors = new HashSet<>();

        checkBlankValue(errors, TASK, "name", task.getName());

        errors.addAll(validateTrigger(task.getTrigger()));
        errors.addAll(validateAction(task.getAction()));

        checkNullValue(errors, TASK, "actionInputFields", task.getActionInputFields());
        errors.addAll(validateDateFormat(task.getActionInputFields()));

        for (int i = 0; i < task.getFilters().size(); ++i) {
            errors.addAll(validateFilter(i, task.getFilters().get(i)));
        }

        for (Map.Entry<String, List<TaskAdditionalData>> entry : task.getAdditionalData().entrySet()) {
            for (int i = 0; i < entry.getValue().size(); ++i) {
                errors.addAll(validateAdditionalData(entry.getKey(), i, entry.getValue().get(i)));
            }
        }

        return errors;
    }

    public static Set<TaskError> validateByTrigger(Task task, Channel channel) {
        Set<TaskError> errors = new HashSet<>();
        TaskEventInformation triggerInformation = task.getTrigger();
        boolean exists = channel.containsTrigger(triggerInformation);

        if (exists) {
            TriggerEvent triggerEvent = channel.getTrigger(triggerInformation);

            errors.addAll(validateActionInputFields(task, triggerEvent));
            errors.addAll(validateFilters(task, triggerEvent));
            errors.addAll(validateAdditionalDatas(task, triggerEvent));
        } else {
            errors.add(new TaskError("validation.error.triggerNotExist", triggerInformation.getDisplayName(), channel.getDisplayName()));
        }

        return errors;
    }

    public static Set<TaskError> validateByAction(Task task, Channel channel) {
        Set<TaskError> errors = new HashSet<>();
        TaskActionInformation actionInformation = task.getAction();
        boolean exists = channel.containsAction(actionInformation);

        if (exists) {
            ActionEvent actionEvent = channel.getAction(actionInformation);

            errors.addAll(validateActionInputFields(task, actionEvent));
        } else {
            errors.add(new TaskError("validation.error.actionNotExist", actionInformation.getDisplayName(), channel.getDisplayName()));
        }

        return errors;
    }

    public static Set<TaskError> validateByProvider(Task task, TaskDataProvider provider) {
        Set<TaskError> errors = new HashSet<>();
        errors.addAll(validateAdditionalDatas(task, provider));
        errors.addAll(validateActionInputFields(task, provider));

        return errors;
    }

    private static Set<TaskError> validateFilters(Task task, TriggerEvent triggerEvent) {
        Set<TaskError> errors = new HashSet<>();

        for (Filter filter : task.getFilters()) {
            if (!triggerEvent.containsParameter(filter.getEventParameter().getEventKey())) {
                errors.add(new TaskError("validation.error.triggerFieldNotExist", filter.getEventParameter().getEventKey(), triggerEvent.getDisplayName()));
            }
        }

        return errors;
    }

    private static Set<TaskError> validateActionInputFields(Task task, TriggerEvent triggerEvent) {
        Set<TaskError> errors = new HashSet<>();

        for (String input : task.getActionInputFields().values()) {
            for (KeyInformation key : KeyInformation.parseAll(input)) {
                if (key.fromTrigger() && !triggerEvent.containsParameter(key.getKey())) {
                    errors.add(new TaskError("validation.error.triggerFieldNotExist", key.getKey(), triggerEvent.getDisplayName()));
                }
            }
        }

        return errors;
    }

    private static Set<TaskError> validateActionInputFields(Task task, ActionEvent actionEvent) {
        Set<TaskError> errors = new HashSet<>();

        for (String inputKey : task.getActionInputFields().keySet()) {
            if (!actionEvent.containsParameter(inputKey)) {
                errors.add(new TaskError("validation.error.actionInputFieldNotExist", inputKey, actionEvent.getDisplayName()));
            }
        }

        return errors;
    }

    private static Set<TaskError> validateActionInputFields(Task task, TaskDataProvider provider) {
        Set<TaskError> errors = new HashSet<>();

        for (String input : task.getActionInputFields().values()) {
            for (KeyInformation key : KeyInformation.parseAll(input)) {
                errors.addAll(validateKeyInformation(provider, key));
            }
        }

        return errors;
    }

    private static Set<TaskError> validateDateFormat(Map<String, String> actionInputFields) {
        Set<TaskError> errors = new HashSet<>();

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
                        errors.add(new TaskError("validation.error.dateFormat", String.format("%s.%s", objectFields[objectFields.length - 2], objectFields[objectFields.length - 1]), entry.getKey()));
                    }
                }
            }
        }

        return errors;
    }

    private static Set<TaskError> validateAction(TaskActionInformation action) {
        Set<TaskError> errors = new HashSet<>();

        checkNullValue(errors, TASK, "action", action);

        if (isEmpty(errors)) {
            String objectName = TASK + ".action";

            checkBlankValue(errors, objectName, "channelName", action.getChannelName());
            checkBlankValue(errors, objectName, "moduleName", action.getModuleName());
            checkBlankValue(errors, objectName, MODULE_VERSION, action.getModuleVersion());

            checkVersion(errors, objectName, MODULE_VERSION, action.getModuleVersion());

            if (!action.hasSubject() && !action.hasService()) {
                errors.add(new TaskError("validation.error.taskAction"));
            }
        }

        return errors;
    }

    private static Set<TaskError> validateTrigger(TaskEventInformation trigger) {
        Set<TaskError> errors = new HashSet<>();

        checkNullValue(errors, TASK, "action", trigger);

        if (isEmpty(errors)) {
            String objectName = TASK + ".trigger";

            checkBlankValue(errors, objectName, "channelName", trigger.getChannelName());
            checkBlankValue(errors, objectName, "moduleName", trigger.getModuleName());
            checkBlankValue(errors, objectName, MODULE_VERSION, trigger.getModuleVersion());
            checkBlankValue(errors, objectName, "subject", trigger.getSubject());

            checkVersion(errors, objectName, MODULE_VERSION, trigger.getModuleVersion());
        }

        return errors;
    }

    private static Set<TaskError> validateFilter(int index, Filter filter) {
        Set<TaskError> errors = new HashSet<>();
        String field = "filters[" + index + "]";

        checkNullValue(errors, TASK, field, filter);

        if (isEmpty(errors)) {
            String objectName = "task." + field;

            checkBlankValue(errors, objectName, "operator", filter.getOperator());
            checkBlankValue(errors, objectName, "expression", filter.getExpression());

            errors.addAll(validateEventParameter(objectName, "eventParameter", filter.getEventParameter()));
        }

        return errors;
    }

    private static Set<TaskError> validateAdditionalDatas(Task task, TriggerEvent triggerEvent) {
        Set<TaskError> errors = new HashSet<>();

        for (List<TaskAdditionalData> list : task.getAdditionalData().values()) {
            for (TaskAdditionalData tad : list) {
                KeyInformation key = KeyInformation.parse(tad.getLookupValue());

                if (key.fromTrigger() && !triggerEvent.containsParameter(key.getKey())) {
                    errors.add(new TaskError("validation.error.triggerFieldNotExist", key.getKey(), triggerEvent.getDisplayName()));
                }
            }
        }

        return errors;
    }

    private static Set<TaskError> validateAdditionalDatas(Task task, TaskDataProvider provider) {
        Set<TaskError> errors = new HashSet<>();

        for (TaskAdditionalData object : task.getAdditionalData(provider.getId())) {
            boolean contains = provider.containsProviderObject(object.getType());

            if (!contains) {
                errors.add(new TaskError("validation.error.providerObjectNotExist", object.getType(), provider.getName()));
            }

            if (contains && !provider.containsProviderObjectLookup(object.getType(), object.getLookupField())) {
                errors.add(new TaskError("validation.error.providerObjectLookupNotExist", object.getLookupField(), object.getType(), provider.getName()));
            }

            KeyInformation key = KeyInformation.parse(object.getLookupValue());
            errors.addAll(validateKeyInformation(provider, key));
        }

        return errors;
    }

    private static Set<TaskError> validateKeyInformation(TaskDataProvider provider, KeyInformation key) {
        Set<TaskError> errors = new HashSet<>();

        if (key.fromAdditionalData() && equalsIgnoreCase(key.getDataProviderId(), provider.getId())) {
            if (provider.containsProviderObject(key.getObjectType())) {
                TaskDataProviderObject providerObject = provider.getProviderObject(key.getObjectType());

                if (!providerObject.containsField(key.getKey())) {
                    errors.add(new TaskError("validation.error.providerObjectFieldNotExist", key.getKey(), providerObject.getDisplayName()));
                }
            } else {
                errors.add(new TaskError("validation.error.providerObjectNotExist", key.getObjectType(), provider.getName()));
            }
        }

        return errors;
    }

    private static Set<TaskError> validateAdditionalData(String key, int index, TaskAdditionalData additionalData) {
        Set<TaskError> errors = new HashSet<>();
        String field = "additionalData[" + key + "][" + index + "]";

        checkNullValue(errors, TASK, field, additionalData);

        if (isEmpty(errors)) {
            String objectName = "task." + field;

            checkNullValue(errors, objectName, "id", additionalData.getId());

            checkBlankValue(errors, objectName, "type", additionalData.getType());
            checkBlankValue(errors, objectName, "lookupField", additionalData.getLookupField());
            checkBlankValue(errors, objectName, "lookupValue", additionalData.getLookupValue());
        }

        return errors;
    }

}
