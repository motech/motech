package org.motechproject.tasks.validation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.DataSource;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.FilterSet;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.domain.OperatorType;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskConfig;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.domain.TaskEventInformation;
import org.motechproject.tasks.domain.TriggerEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.motechproject.tasks.domain.KeyInformation.parse;

public final class TaskValidator extends GeneralValidator {
    public static final String TASK = "task";

    private static final String MODULE_VERSION = "moduleVersion";

    private TaskValidator() {
    }

    public static Set<TaskError> validate(Task task) {
        Set<TaskError> errors = new HashSet<>();

        checkBlankValue(errors, TASK, "name", task.getName());

        errors.addAll(validateTrigger(task.getTrigger()));

        checkEmpty(errors, TASK, "actions", task.getActions());

        for (int i = 0; i < task.getActions().size(); ++i) {
            errors.addAll(validateAction(i, task.getActions().get(i)));
        }

        errors.addAll(validateTaskConfig(task.getTaskConfig()));

        return errors;
    }

    public static Set<TaskError> validateTrigger(Task task, Channel channel) {
        Set<TaskError> errors = new HashSet<>();
        TaskEventInformation triggerInformation = task.getTrigger();
        boolean exists = channel.containsTrigger(triggerInformation);

        if (exists) {
            TriggerEvent triggerEvent = channel.getTrigger(triggerInformation);

            for (TaskActionInformation action : task.getActions()) {
                errors.addAll(validateActionValues(action.getValues(), triggerEvent));
            }

            errors.addAll(validateFiltersForTrigger(task, triggerEvent));
        } else {
            errors.add(new TaskError(
                    "task.validation.error.triggerNotExist",
                    triggerInformation.getDisplayName(),
                    channel.getDisplayName()
            ));
        }

        return errors;
    }

    public static Set<TaskError> validateAction(TaskActionInformation actionInformation,
                                                Channel channel) {
        Set<TaskError> errors = new HashSet<>();
        boolean exists = channel.containsAction(actionInformation);

        if (exists) {
            ActionEvent actionEvent = channel.getAction(actionInformation);

            errors.addAll(validateActionValues(actionInformation.getValues(), actionEvent));
        } else {
            errors.add(new TaskError(
                    "task.validation.error.actionNotExist",
                    actionInformation.getDisplayName(),
                    channel.getDisplayName()
            ));
        }

        return errors;
    }

    public static Set<TaskError> validateProvider(Map<String, String> actionValues,
                                                  DataSource dataSource,
                                                  TaskDataProvider provider,
                                                  SortedSet<FilterSet> filterSets) {
        Set<TaskError> errors = new HashSet<>();
        errors.addAll(validateDataSource(dataSource, provider));
        errors.addAll(validateActionValues(actionValues, provider));

        return errors;
    }

    private static Set<TaskError> validateFiltersForTrigger(Task task, TriggerEvent triggerEvent) {
        Set<TaskError> errors = new HashSet<>();

        for (FilterSet filterSet : task.getTaskConfig().getFilters()) {
            for (Filter filter : filterSet.getFilters()) {
                KeyInformation key = parse(filter.getKey());

                if (key.fromTrigger() && !triggerEvent.containsParameter(key.getKey())) {
                    errors.add(new TaskError(
                            "task.validation.error.triggerFieldNotExist",
                            key.getKey(),
                            triggerEvent.getDisplayName()
                    ));
                }
            }
        }

        return errors;
    }

    private static Set<TaskError> validateActionValues(Map<String, String> actionValues,
                                                       TriggerEvent triggerEvent) {
        Set<TaskError> errors = new HashSet<>();

        for (String input : actionValues.values()) {
            for (KeyInformation key : KeyInformation.parseAll(input)) {
                if (key.fromTrigger() && !triggerEvent.containsParameter(key.getKey())) {
                    errors.add(new TaskError(
                            "task.validation.error.triggerFieldNotExist",
                            key.getKey(),
                            triggerEvent.getDisplayName()
                    ));
                }
            }
        }

        return errors;
    }

    private static Set<TaskError> validateActionValues(Map<String, String> actionValues,
                                                       ActionEvent actionEvent) {
        Set<TaskError> errors = new HashSet<>();

        for (String inputKey : actionValues.keySet()) {
            if (!actionEvent.containsParameter(inputKey)) {
                errors.add(new TaskError(
                        "task.validation.error.actionInputFieldNotExist",
                        inputKey,
                        actionEvent.getDisplayName()
                ));
            }
        }

        return errors;
    }

    private static Set<TaskError> validateActionValues(Map<String, String> actionValues,
                                                       TaskDataProvider provider) {
        Set<TaskError> errors = new HashSet<>();

        for (String input : actionValues.values()) {
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
                        errors.add(new TaskError(
                                "task.validation.error.dateFormat",
                                String.format(
                                        "%s.%s",
                                        objectFields[objectFields.length - 2],
                                        objectFields[objectFields.length - 1]
                                ),
                                entry.getKey()
                        ));
                    }
                }
            }
        }

        return errors;
    }

    private static Set<TaskError> validateAction(int idx, TaskActionInformation action) {
        Set<TaskError> errors = new HashSet<>();

        checkNullValue(errors, TASK, String.format("actions[%d]", idx), action);

        if (isEmpty(errors)) {
            String objectName = String.format("%s.actions[%d]", TASK, idx);

            checkBlankValue(errors, objectName, "channelName", action.getChannelName());
            checkBlankValue(errors, objectName, "moduleName", action.getModuleName());
            checkBlankValue(errors, objectName, MODULE_VERSION, action.getModuleVersion());

            checkVersion(errors, objectName, MODULE_VERSION, action.getModuleVersion());

            if (!action.hasSubject() && !action.hasService()) {
                errors.add(new TaskError("task.validation.error.taskAction"));
            }

            checkNullValue(errors, objectName, "values", action.getValues());

            for (Map.Entry<String, String> entry : action.getValues().entrySet()) {
                checkBlankValue(
                        errors, String.format("%s.values", objectName),
                        entry.getKey(), entry.getValue()
                );
            }

            errors.addAll(validateDateFormat(action.getValues()));
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

    private static Set<TaskError> validateFilter(Integer setOrder, int index, Filter filter,
                                                 TaskConfig config) {
        Set<TaskError> errors = new HashSet<>();
        String field = String.format("taskConfig.filterSet[%d].filters[%d]", setOrder, index);
        KeyInformation key = parse(filter.getKey());
        DataSource dataSource = config.getDataSource(
                key.getDataProviderId(), key.getObjectId(), key.getObjectType()
        );

        checkNullValue(errors, TASK, field, filter);

        if (isEmpty(errors)) {
            String objectName = "task." + field;

            if (key.fromAdditionalData() && dataSource == null) {
                errors.add(new TaskError(
                        "task.validation.error.DataSourceNotExist",
                        key.getObjectType()
                ));
            }

            checkBlankValue(errors, objectName, "key", filter.getKey());
            checkBlankValue(errors, objectName, "displayName", filter.getDisplayName());
            checkBlankValue(errors, objectName, "operator", filter.getOperator());

            checkNullValue(errors, objectName, "type", filter.getType());

            if (OperatorType.fromString(filter.getOperator()) != OperatorType.EXIST) {
                checkBlankValue(errors, objectName, "expression", filter.getExpression());
            }
        }

        return errors;
    }

    private static Set<TaskError> validateDataSource(DataSource dataSource,
                                                     TaskDataProvider provider) {
        Set<TaskError> errors = new HashSet<>();

        boolean contains = provider.containsProviderObject(dataSource.getType());
        for (DataSource.Lookup lookup : dataSource.getLookup()) {
            if (!contains) {
                errors.add(new TaskError(
                        "task.validation.error.providerObjectNotExist",
                        dataSource.getType(),
                        provider.getName()
                ));
            } else if (!provider.containsProviderObjectLookup(dataSource.getType(), dataSource.getName())) {
                errors.add(new TaskError(
                        "task.validation.error.providerObjectLookupNotExist",
                        lookup.getField(),
                        dataSource.getType(),
                        provider.getName()
                ));
            }

            String lookupValue = lookup.getValue();

            if (lookupValue != null) {
                for (KeyInformation key : KeyInformation.parseAll(lookupValue)) {
                    errors.addAll(validateKeyInformation(provider, key));
                }
            }
        }

        return errors;
    }

    private static Set<TaskError> validateKeyInformation(TaskDataProvider provider,
                                                         KeyInformation key) {
        Set<TaskError> errors = new HashSet<>();

        if (equalsIgnoreCase(key.getDataProviderId(), provider.getId())) {
            if (!provider.containsProviderObject(key.getObjectType())) {
                errors.add(new TaskError(
                        "task.validation.error.providerObjectNotExist",
                        key.getObjectType(),
                        provider.getName()
                ));
            }
        }

        return errors;
    }

    private static Set<TaskError> validateDataSource(DataSource dataSource) {
        Set<TaskError> errors = new HashSet<>();
        String field = "taskConfig.dataSource[" + dataSource.getOrder() + "]";
        for (DataSource.Lookup lookup : dataSource.getLookup()) {
            if (isEmpty(errors)) {
                String objectName = "task." + field;

                checkNullValue(errors, objectName, "objectId", dataSource.getObjectId());

                checkBlankValue(errors, objectName, "providerId", dataSource.getProviderId());
                checkBlankValue(errors, objectName, "type", dataSource.getType());
                checkBlankValue(errors, objectName, "lookup.field", lookup.getField());
                checkBlankValue(errors, objectName, "lookup.value", lookup.getValue());
            }
        }
        return errors;
    }

    private static Set<TaskError> validateTaskConfig(TaskConfig config) {
        Set<TaskError> errors = new HashSet<>();

        for (FilterSet filterSet : config.getFilters()) {
            List<Filter> filters = filterSet.getFilters();

            for (int i = 0; i < filters.size(); ++i) {
                errors.addAll(validateFilter(filterSet.getOrder(), i, filters.get(i), config));
            }
        }

        for (DataSource dataSource : config.getDataSources()) {
            errors.addAll(validateDataSource(dataSource));
        }

        return errors;
    }

}
