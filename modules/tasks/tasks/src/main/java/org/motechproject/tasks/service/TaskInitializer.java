package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.commons.api.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.DataSource;
import org.motechproject.tasks.domain.FilterSet;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskConfigStep;
import org.motechproject.tasks.ex.TaskHandlerException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.tasks.domain.KeyInformation.ADDITIONAL_DATA_PREFIX;
import static org.motechproject.tasks.domain.KeyInformation.TRIGGER_PREFIX;
import static org.motechproject.tasks.events.constants.TaskFailureCause.DATA_SOURCE;
import static org.motechproject.tasks.events.constants.TaskFailureCause.FILTER;
import static org.motechproject.tasks.events.constants.TaskFailureCause.TRIGGER;
import static org.motechproject.tasks.service.HandlerUtil.DATETIME_PATTERN_BEGIN_INDEX;
import static org.motechproject.tasks.service.HandlerUtil.FORMAT_PATTERN_BEGIN_INDEX;

/**
 * The <code>TaskInitializer</code> class provides methods which prepare an action in the task
 * definition to execution.
 * <p/>
 * <ul>
 * <li><b>evalConfigSteps</b> - executes all config steps (load data sources, check filters) defined in the task,</li>
 * <li><b>createParameters</b> - creates correct parameters for an action.</li>
 * </ul>
 *
 * @see TaskTriggerHandler
 * @see TaskExecutor
 * @since 0.20
 */
class TaskInitializer {
    private Map<String, Object> dataSourceObjects;
    private Task task;
    private MotechEvent event;
    private TaskActivityService activityService;

    TaskInitializer(Task task, MotechEvent event, TaskActivityService activityService) {
        this.task = task;
        this.event = event;
        this.activityService = activityService;

        this.dataSourceObjects = new HashMap<>();
    }

    boolean evalConfigSteps(Map<String, DataProvider> dataProviders) throws TaskHandlerException {
        Iterator<TaskConfigStep> iterator = task.getTaskConfig().getSteps().iterator();
        boolean result = true;

        while (result && iterator.hasNext()) {
            TaskConfigStep step = iterator.next();

            if (step instanceof DataSource) {
                DataSource ds = (DataSource) step;
                dataSourceObjects.put(
                        ds.getObjectId().toString(), getDataSourceObject(ds, dataProviders)
                );
            } else if (step instanceof FilterSet) {
                try {
                    result = passFilters((FilterSet) step);
                } catch (Exception e) {
                    throw new TaskHandlerException(FILTER, "task.error.filterError", e);
                }
            }
        }

        return result;
    }

    Map<String, Object> createParameters(TaskActionInformation info,
                                         ActionEvent action) throws TaskHandlerException {
        SortedSet<ActionParameter> actionParameters = action.getActionParameters();
        Map<String, Object> parameters = new HashMap<>(actionParameters.size());

        for (ActionParameter param : actionParameters) {
            String key = param.getKey();

            if (info.getValues().containsKey(key)) {
                String template = info.getValues().get(key);

                if (template == null) {
                    throw new TaskHandlerException(
                            TRIGGER, "task.error.templateNull", key, action.getDisplayName()
                    );
                }

                switch (param.getType()) {
                    case LIST:
                        parameters.put(key, convertToList(template));
                        break;
                    case MAP:
                        parameters.put(key, convertToMap(template));
                        break;
                    case DATE:
                        try {
                            String userInput = convert(template);
                            String pattern = getPattern(template);
                            Object obj = HandlerUtil.convertTo(param.getType(), userInput, pattern);
                            parameters.put(key, obj);
                        } catch (MotechException ex) {
                            throw new TaskHandlerException(TRIGGER, ex.getMessage(), ex, key);
                        }
                        break;
                    default:
                        try {
                            String userInput = convert(template);
                            Object obj = HandlerUtil.convertTo(param.getType(), userInput, null);
                            parameters.put(key, obj);
                        } catch (MotechException ex) {
                            throw new TaskHandlerException(TRIGGER, ex.getMessage(), ex, key);
                        }
                }
            } else {
                if (param.isRequired()) {
                    throw new TaskHandlerException(
                            TRIGGER, "task.error.taskActionNotContainsField",
                            action.getDisplayName(), key
                    );
                } else {
                    parameters.put(key, null);
                }
            }
        }

        return parameters;
    }

    private String getPattern(String template) {
        if (template.startsWith("{{") && template.endsWith("}}")) {
            if (template.contains("?")) {
                return template.substring(template.indexOf('?') + DATETIME_PATTERN_BEGIN_INDEX + 1, template.indexOf("}}") - 1);
            } else {
                return "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
            }
        } else {
            return "yyyy-MM-dd HH:mm Z";
        }
    }

    private boolean passFilters(FilterSet filterSet) throws TaskHandlerException {
        boolean result;

        try {
            result = HandlerUtil.checkFilters(
                    filterSet.getFilters(), event.getParameters(), dataSourceObjects
            );
        } catch (Exception e) {
            throw new TaskHandlerException(FILTER, "task.error.filterError", e);
        }

        return result;
    }

    private Object getDataSourceObject(DataSource dataSource, Map<String, DataProvider> providers)
            throws TaskHandlerException {
        if (providers == null || providers.isEmpty()) {
            throw new TaskHandlerException(
                    DATA_SOURCE, "task.error.notFoundDataProvider", dataSource.getType()
            );
        }

        DataProvider provider = providers.get(dataSource.getProviderId());

        if (provider == null) {
            throw new TaskHandlerException(
                    DATA_SOURCE, "task.error.notFoundDataProvider", dataSource.getType()
            );
        }


        Map<String, String> lookupFields = new HashMap<>();
        for (DataSource.Lookup lookup : dataSource.getLookup()) {
            lookupFields.put(lookup.getField(), convert(lookup.getValue()));
        }

        return provider.lookup(dataSource.getType(), lookupFields);
    }

    private String convert(String template) throws TaskHandlerException {
        String conversionTemplate = template;

        for (KeyInformation key : KeyInformation.parseAll(template)) {
            String value = getValue(key);

            if (key.hasManipulations()) {
                value = manipulateValue(value, key.getManipulations());
            }

            conversionTemplate = conversionTemplate.replace(
                    String.format("{{%s}}", key.getOriginalKey()), value
            );
        }

        return conversionTemplate;
    }

    private String manipulateValue(String value,
                                   List<String> manipulations) throws TaskHandlerException {
        String manipulateValue = value;

        for (String manipulation : manipulations) {
            if (manipulation.contains("format")) {
                String formatElements = manipulation.substring(FORMAT_PATTERN_BEGIN_INDEX, manipulation.length() - 1);

                if (isNotBlank(formatElements)) {
                    String[] items = formatElements.split(",");

                    for (int i = 0; i < items.length; ++i) {
                        String key = items[i];

                        if (key.startsWith("{{") && key.endsWith("}}")) {
                            key = key.substring(2, key.length() - 2);
                            items[i] = getValue(KeyInformation.parse(key));
                        }
                    }

                    manipulateValue = String.format(manipulateValue, items);
                }
            } else {
                try {
                    manipulateValue = HandlerUtil.manipulate(manipulation, manipulateValue);
                } catch (MotechException e) {
                    String msg = e.getMessage();

                    if ("task.warning.manipulation".equalsIgnoreCase(msg)) {
                        activityService.addWarning(task, msg, manipulation);
                    } else {
                        throw new TaskHandlerException(TRIGGER, msg, e, manipulation);
                    }
                }
            }
        }

        return manipulateValue;
    }

    private Object getDataSourceObjectValue(KeyInformation key) throws TaskHandlerException {
        DataSource dataSource = task.getTaskConfig().getDataSource(
                key.getDataProviderId(), key.getObjectId(), key.getObjectType()
        );
        Object found = dataSourceObjects.get(dataSource.getObjectId().toString());
        Object value;

        if (found == null) {
            if (dataSource.isFailIfDataNotFound()) {
                throw new TaskHandlerException(
                        DATA_SOURCE, "task.error.notFoundObjectForType", key.getObjectType()
                );
            } else {
                activityService.addWarning(
                        task, "task.warning.notFoundObjectForType", key.getObjectType()
                );
            }
        }

        try {
            value = HandlerUtil.getFieldValue(found, key.getKey());
        } catch (Exception e) {
            if (dataSource.isFailIfDataNotFound()) {
                throw new TaskHandlerException(
                        DATA_SOURCE, "task.error.objectNotContainsField", e, key.getKey()
                );
            } else {
                activityService.addWarning(task, "task.warning.objectNotContainsField", key.getKey(), e);
                value = "";
            }
        }

        return value;
    }

    private Map<Object, Object> convertToMap(String template) throws TaskHandlerException {
        String[] rows = template.split("(\\r)?\\n");
        Map<Object, Object> tempMap = new HashMap<>(rows.length);

        for (String row : rows) {
            String[] array = row.split(":", 2);
            Object mapKey;
            Object mapValue;

            switch (array.length) {
                case 2:
                    array[1] = array[1].trim();
                    mapKey = getValue(array[0]);
                    mapValue = getValue(array[1]);

                    tempMap.put(
                            HandlerUtil.convertTo(mapKey.getClass(), convert(array[0]), DateTime.class.equals(mapKey.getClass())? getPattern(array[0]) : null),
                            HandlerUtil.convertTo(mapValue.getClass(), convert(array[1]), DateTime.class.equals(mapValue.getClass())? getPattern(array[1]) : null)
                    );
                    break;
                case 1:
                    mapValue = getValue(array[0]);

                    tempMap.putAll((Map) mapValue);
                    break;
                default:
            }
        }
        return tempMap;
    }

    private List<Object> convertToList(String template) throws TaskHandlerException {
        String[] rows = template.split("(\\r)?\\n");
        List<Object> tempList = new ArrayList<>(rows.length);

        for (String row : rows) {
            Object value = getValue(row.trim());

            if (value instanceof Collection) {
                tempList.addAll((Collection) value);
            } else {
                tempList.add(HandlerUtil.convertTo(value.getClass(), convert(row.trim()), DateTime.class.equals(value.getClass())? getPattern(row.trim()) : null));
            }
        }

        return tempList;
    }

    private Object getValue(String row) throws TaskHandlerException {
        List<KeyInformation> keys = KeyInformation.parseAll(row);
        Object result = null;

        if (keys.isEmpty()) {
            result = row;
        } else {
            KeyInformation rowKeyInfo = keys.get(0);

            switch (rowKeyInfo.getPrefix()) {
                case TRIGGER_PREFIX:
                    result = event.getParameters().get(rowKeyInfo.getKey());
                    break;
                case ADDITIONAL_DATA_PREFIX:
                    result = getDataSourceObjectValue(rowKeyInfo);
                    break;
                default:
            }
        }

        return result;
    }

    private String getValue(KeyInformation key) throws TaskHandlerException {
        String value = "";

        switch (key.getPrefix()) {
            case TRIGGER_PREFIX:
                try {
                    Object triggerKey = HandlerUtil.getTriggerKey(event, key);
                    value = triggerKey == null ? "" : triggerKey.toString();
                } catch (Exception e) {
                    throw new TaskHandlerException(
                            TRIGGER, "task.error.objectNotContainsField", e, key.getKey()
                    );
                }
                break;
            case ADDITIONAL_DATA_PREFIX:
                Object additionalDataValue = getDataSourceObjectValue(key);
                value = additionalDataValue == null ? "" : additionalDataValue.toString();
                break;
            default:
        }

        return value;
    }

    Task getTask() {
        return task;
    }
}
