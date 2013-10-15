package org.motechproject.tasks.validation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
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
import org.motechproject.tasks.domain.ManipulationType;
import org.motechproject.tasks.domain.ManipulationTarget;
import org.motechproject.tasks.domain.ParameterType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.motechproject.tasks.domain.KeyInformation.parse;

/**
 * The <code>TaskValidator</code> class is responsible for task validation
 */

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

        if (!exists) {
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


        if (!exists) {
            errors.add(new TaskError(
                    "task.validation.error.actionNotExist",
                    actionInformation.getDisplayName(),
                    channel.getDisplayName()
            ));
        }

        return errors;
    }

    public static Set<TaskError> validateProvider(TaskDataProvider provider, DataSource dataSource, TriggerEvent trigger, Map<String, TaskDataProvider> availableProviders) {
        Set<TaskError> errors = new HashSet<>();
        Map<String, String> fields = new HashMap<>();
        Map<String, ParameterType> fieldsTypes = new HashMap<>();

        if (!provider.containsProviderObject(dataSource.getType())) {
            errors.add(new TaskError(
                    "task.validation.error.providerObjectNotExist",
                    dataSource.getType(),
                    provider.getName()
            ));
        } else {
            for (DataSource.Lookup lookup : dataSource.getLookup()) {
                if (!provider.containsProviderObjectLookup(dataSource.getType(), dataSource.getName())) {
                    errors.add(new TaskError(
                            "task.validation.error.providerObjectLookupNotExist",
                            lookup.getField(),
                            dataSource.getType(),
                            provider.getName()
                    ));
                }
                fields.put(lookup.getField(), lookup.getValue());
                fieldsTypes.put(lookup.getField(), ParameterType.UNKNOWN);
            }

            errors.addAll(validateFieldsParameter(fields, fieldsTypes, trigger, availableProviders));
        }

        return errors;
    }

    public static Set<TaskError> validateActionFields(TaskActionInformation action, ActionEvent actionEvent, TriggerEvent trigger, Map<String, TaskDataProvider> providers) {
        Map<String, String> fields = action.getValues();
        Map<String, ParameterType> fieldsTypes = new HashMap<>();

        for (ActionParameter param : actionEvent.getActionParameters()) {
            fieldsTypes.put(param.getKey(), param.getType());
        }

        return validateFieldsParameter(fields, fieldsTypes, trigger, providers);
    }

    private static Set<TaskError> validateFieldsParameter(Map<String, String> fields, Map<String, ParameterType> fieldsTypes, TriggerEvent trigger, Map<String, TaskDataProvider> providers) {
        Set<TaskError> errors = new HashSet<>();

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (entry.getValue() != null) {
                for (KeyInformation key : KeyInformation.parseAll(entry.getValue())) {
                    errors.addAll(validateKeyInformation(key, fieldsTypes.get(entry.getKey()), trigger, providers));
                }
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

            if (OperatorType.needExpression(filter.getOperator())) {
                checkBlankValue(errors, objectName, "expression", filter.getExpression());
            }
        }

        return errors;
    }

    private static Set<TaskError> validateKeyInformation(KeyInformation key, ParameterType fieldType, TriggerEvent trigger, Map<String, TaskDataProvider> providers) {
        Set<TaskError> errors = new HashSet<>();

        if (key.fromTrigger() && trigger.containsParameter(key.getKey()) && key.hasManipulations()) {
            for (String manipulation : key.getManipulations()) {
                errors.addAll(validateManipulations(manipulation,
                        key,
                        ParameterType.fromString(trigger.getKeyType(key.getKey())),
                        fieldType
                ));
            }
        } else if (key.fromAdditionalData() && providers.containsKey(key.getDataProviderId()) && providers.get(key.getDataProviderId()).containsProviderObjectField(key.getObjectType(), key.getKey()) && key.hasManipulations()) {
            for (String manipulations : key.getManipulations()) {
                errors.addAll(validateManipulations(manipulations,
                        key,
                        ParameterType.fromString(providers.get(key.getDataProviderId()).getKeyType(key.getKey())),
                        fieldType
                ));
            }
        }

        return errors;
    }

    private static Set<TaskError> validateManipulations(String manipulation, KeyInformation key, ParameterType parameterType, ParameterType fieldType) {
        Set<TaskError> errors = new HashSet<>();
        TaskError error;
        String at = key.getKey() + "?" + manipulation;

        if (parameterType == ParameterType.UNICODE || parameterType == ParameterType.TEXTAREA || parameterType == ParameterType.UNKNOWN) {
            error = validateStringManipulation(manipulation, at);

            if (error != null) {
                errors.add(error);
            }
        } else if (parameterType == ParameterType.DATE) {
            error = validateDateManipulation(manipulation, fieldType, at);

            if (error != null) {
                errors.add(error);
            }
        } else {
            errors.add(new TaskError(
                    "task.validation.error.wrongAnotherManipulation",
                    manipulation,
                    at
            ));
        }

        return errors;
    }

    private static TaskError validateStringManipulation(String manipulation, String foundAt) {
        TaskError error = null;
        ManipulationType type = ManipulationType.fromString(manipulation.replaceAll("\\((.*?)\\)", ""));

        if (type.getTarget() != ManipulationTarget.STRING) {
            if (type.getTarget() == ManipulationTarget.ALL) {
                error = new TaskError(
                        "task.validation.error.wrongAnotherManipulation",
                        manipulation,
                        foundAt
                );
            } else {
                error = new TaskError(
                        "task.validation.error.wrongStringManipulation",
                        manipulation,
                        foundAt
                );
            }
        }

        return error;
    }

    private static TaskError validateDateManipulation(String manipulation, ParameterType fieldType, String foundAt) {
        TaskError error = null;
        ManipulationType type = ManipulationType.fromString(manipulation.replaceAll("\\((.*?)\\)", ""));

        if (type.getTarget() != ManipulationTarget.DATE) {
            if (type.getTarget() == ManipulationTarget.ALL) {
                error = new TaskError(
                        "task.validation.error.wrongAnotherManipulation",
                        manipulation,
                        foundAt
                );
            } else {
                error = new TaskError(
                        "task.validation.error.wrongDateManipulation",
                        manipulation,
                        foundAt
                );
            }
        } else if ( fieldType.equals(ParameterType.DATE) && !type.allowResultType(ManipulationTarget.DATE)) {
            error = new TaskError(
                    "task.validation.error.wrongDateManipulationTarget",
                    manipulation,
                    foundAt
            );
        }

        return error;
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
