package org.motechproject.tasks.validation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.domain.ManipulationTarget;
import org.motechproject.tasks.domain.ManipulationType;
import org.motechproject.tasks.domain.enums.ParameterType;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.channel.ActionParameter;
import org.motechproject.tasks.domain.mds.channel.Channel;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.tasks.domain.mds.task.DataSource;
import org.motechproject.tasks.domain.mds.task.Filter;
import org.motechproject.tasks.domain.mds.task.FilterSet;
import org.motechproject.tasks.domain.mds.task.Lookup;
import org.motechproject.tasks.domain.mds.task.OperatorType;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.TaskConfig;
import org.motechproject.tasks.domain.mds.task.TaskDataProvider;
import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.service.TriggerEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
 * Utility class for validating tasks.
 */
@Component
public class TaskValidator extends GeneralValidator {

    public static final String TASK = "task";

    private static final String MODULE_VERSION = "moduleVersion";

    private TriggerEventService triggerEventService;

    /**
     * Validates the given task by checking if all necessary data is set. Returns the set of TaskError containing
     * information about missing fields.
     *
     * @param task  the task to be validated, not null
     * @return  the set of encountered errors
     */
    @Transactional
    public Set<TaskError> validate(Task task) {
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

    /**
     * Validates the trigger of the given task by checking if it is specified in the given channel.
     *
     * @param task  the task for which the trigger should be validated, not null
     * @return  the set of encountered errors
     */
    @Transactional
    public Set<TaskError> validateTrigger(Task task) {
        Set<TaskError> errors = new HashSet<>();
        TaskTriggerInformation triggerInformation = task.getTrigger();
        boolean exists = triggerEventService.triggerExists(triggerInformation);

        if (!exists) {
            errors.add(new TaskError(
                    "task.validation.error.triggerNotExist",
                    triggerInformation.getDisplayName()
            ));
        }

        return errors;
    }

    /**
     * Checks if the channel contains the given actions.
     *
     * @param actionInformation  the information about action, not null
     * @param channel  the channel to be checked, not null
     * @return  the set of encountered errors
     */
    @Transactional
    public Set<TaskError> validateAction(TaskActionInformation actionInformation, Channel channel) {
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

    /**
     * Validates if the given provider contains the given data source and trigger event.
     *
     * @param provider  the provider to be checked, not null
     * @param dataSource  the data source to be validated, not null
     * @param trigger  the trigger to be validated, not null
     * @param availableProviders  the map of the IDs and the providers, not null
     * @return  the set of encountered errors
     */
    @Transactional
    public Set<TaskError> validateProvider(TaskDataProvider provider, DataSource dataSource, TriggerEvent trigger,
                                                  Map<Long, TaskDataProvider> availableProviders) {
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
            for (Lookup lookup : dataSource.getLookup()) {
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

    /**
     * Validates whether fields of the the action are properly set.
     *
     * @param action  the information about action, not null
     * @param actionEvent  the action event, not null
     * @param trigger  the trigger of the task the action belongs to, not null
     * @param providers  the map of IDs and providers, not null
     * @return  the set of encountered errors
     */
    @Transactional
    public Set<TaskError> validateActionFields(TaskActionInformation action, ActionEvent actionEvent, TriggerEvent trigger, Map<Long, TaskDataProvider> providers) {
        Map<String, String> fields = action.getValues();
        Map<String, ParameterType> fieldsTypes = new HashMap<>();

        for (ActionParameter param : actionEvent.getActionParameters()) {
            fieldsTypes.put(param.getKey(), param.getType());
        }

        return validateFieldsParameter(fields, fieldsTypes, trigger, providers);
    }

    private Set<TaskError> validateFieldsParameter(Map<String, String> fields, Map<String, ParameterType> fieldsTypes, TriggerEvent trigger, Map<Long, TaskDataProvider> providers) {
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

    private Set<TaskError> validateDateFormat(Map<String, String> actionInputFields) {
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

    private Set<TaskError> validateAction(int idx, TaskActionInformation action) {
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

    private Set<TaskError> validateTrigger(TaskTriggerInformation trigger) {
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

    private Set<TaskError> validateFilter(Integer setOrder, int index, Filter filter,
                                                 TaskConfig config) {
        Set<TaskError> errors = new HashSet<>();
        String field = String.format("taskConfig.filterSet[%d].filters[%d]", setOrder, index);
        KeyInformation key = parse(filter.getKey());
        DataSource dataSource = config.getDataSource(
                key.getDataProviderName(), key.getObjectId(), key.getObjectType()
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

    private Set<TaskError> validateKeyInformation(KeyInformation key, ParameterType fieldType, TriggerEvent trigger, Map<Long, TaskDataProvider> providers) {
        Set<TaskError> errors = new HashSet<>();

        if (key.fromTrigger() && trigger.containsParameter(key.getKey()) && key.hasManipulations()) {
            for (String manipulation : key.getManipulations()) {
                errors.addAll(validateManipulations(manipulation,
                        key,
                        ParameterType.fromString(trigger.getKeyType(key.getKey())),
                        fieldType
                ));
            }
        } else if (key.fromAdditionalData() && providers.containsKey(key.getDataProviderName()) && providers.get(key.getDataProviderName()).containsProviderObjectField(key.getObjectType(), key.getKey()) && key.hasManipulations()) {
            for (String manipulations : key.getManipulations()) {
                errors.addAll(validateManipulations(manipulations,
                        key,
                        ParameterType.fromString(providers.get(key.getDataProviderName()).getKeyType(key.getKey())),
                        fieldType
                ));
            }
        }

        return errors;
    }

    private Set<TaskError> validateManipulations(String manipulation, KeyInformation key, ParameterType parameterType, ParameterType fieldType) {
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

    private TaskError validateStringManipulation(String manipulation, String foundAt) {
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

    private TaskError validateDateManipulation(String manipulation, ParameterType fieldType, String foundAt) {
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
        } else if (fieldType.equals(ParameterType.DATE) && !type.allowResultType(ManipulationTarget.DATE)) {
            error = new TaskError(
                    "task.validation.error.wrongDateManipulationTarget",
                    manipulation,
                    foundAt
            );
        }

        return error;
    }

    private Set<TaskError> validateDataSource(DataSource dataSource) {
        Set<TaskError> errors = new HashSet<>();
        String field = "taskConfig.dataSource[" + dataSource.getOrder() + "]";
        for (Lookup lookup : dataSource.getLookup()) {
            if (isEmpty(errors)) {
                String objectName = "task." + field;

                checkNullValue(errors, objectName, "objectId", dataSource.getObjectId());

                checkBlankValue(errors, objectName, "type", dataSource.getType());
                checkBlankValue(errors, objectName, "lookup.field", lookup.getField());
                checkBlankValue(errors, objectName, "lookup.value", lookup.getValue());
            }
        }
        return errors;
    }

    private Set<TaskError> validateTaskConfig(TaskConfig config) {
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

    @Autowired
    public void setTriggerEventService(TriggerEventService triggerEventService) {
        this.triggerEventService = triggerEventService;
    }
}
