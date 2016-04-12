package org.motechproject.tasks.service.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.constants.TaskFailureCause;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.service.TaskActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TaskContext holds task trigger event and data provider lookup objects that are used while executing filters/actions.
 */
public class TaskContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskContext.class);

    private Task task;
    private Map<String, Object> parameters;
    private TaskActivityService activityService;
    private Set<DataSourceObject> dataSourceObjects;

    /**
     * Class constructor.
     *
     * @param task  the task, not null
     * @param parameters  the task parameters
     * @param activityService  the activity service, not null
     */
    public TaskContext(Task task, Map<String, Object> parameters, TaskActivityService activityService) {
        this.task = task;
        this.parameters = parameters;
        this.activityService = activityService;
        this.dataSourceObjects = new HashSet<>();
    }

    /**
     * Adds the given data source to this task.
     *
     * @param objectId  the ID of the object, not null
     * @param dataSourceObject  the result of lookup execution, not null
     * @param failIfDataNotFound  defines whether task should fail if the data wasn't found
     */
    public void addDataSourceObject(String objectId, Object dataSourceObject, boolean failIfDataNotFound) {
        dataSourceObjects.add(new DataSourceObject(objectId, dataSourceObject, failIfDataNotFound));
    }

    /**
     * Returns the value of the trigger with the given key.
     *
     * @param key  the key of the trigger, not null
     * @return  the value of the trigger with the given key
     */
    public Object getTriggerValue(String key) {
        Object value = null;

        if (parameters != null) {
            value = getFieldValue(parameters, key);
        }

        return value;
    }

    /**
     * Returns the value of data source object based on it's field, id and type.
     *
     * @param objectId  the id of the object, not null
     * @param field  the name of the field, not null
     * @param objectType  the type of the object
     * @return  the value of data source object
     * @throws TaskHandlerException
     */
    public Object getDataSourceObjectValue(String objectId, String field, String objectType) throws TaskHandlerException {
        LOGGER.info("Retrieving task data source object: {} with ID: {}", objectType, objectId);

        DataSourceObject dataSourceObject = getDataSourceObject(objectId);
        if (dataSourceObject == null) {
            throw new TaskHandlerException(TaskFailureCause.DATA_SOURCE, "task.error.objectOfTypeNotFound", objectType);
        }

        if (dataSourceObject.getObjectValue() == null) {
            if (dataSourceObject.isFailIfNotFound()) {
                throw new TaskHandlerException(TaskFailureCause.DATA_SOURCE, "task.error.objectOfTypeNotFound", objectType);
            }
            LOGGER.warn("Task data source object of type: {} not found", objectType);
            publishWarningActivity("task.warning.notFoundObjectForType", objectType);
            return null;
        }

        try {
            return getFieldValue(dataSourceObject.getObjectValue(), field);
        } catch (RuntimeException e) {
            if (dataSourceObject.isFailIfNotFound()) {
                throw new TaskHandlerException(TaskFailureCause.DATA_SOURCE, "task.error.objectDoesNotContainField", e, field);
            }
            LOGGER.warn("Task data source object: {} does not contain field: {}", objectType, field);
            publishWarningActivity("task.warning.objectNotContainsField", field);
        }
        return null;
    }

    /**
     * Publishes warning activity for this task.
     *
     * @param message  the message to be published
     * @param field  the name of the field
     */
    public void publishWarningActivity(String message, String field) {
        activityService.addWarning(task, message, field);
    }

    public Task getTask() {
        return task;
    }

    public Map<String, Object> getTriggerParameters() {
        return parameters;
    }

    private DataSourceObject getDataSourceObject(String objectId) {
        for (DataSourceObject dataSourceObject : dataSourceObjects) {
            if (dataSourceObject.getObjectId().equals(objectId)) {
                return dataSourceObject;
            }
        }
        return null;
    }

    private Object getFieldValue(Object object, String field) {
        String[] subFields = field.split("\\.");
        Object current = object;

        for (String subField : subFields) {
            if (current == null) {
                throw new IllegalStateException("Field on path is null");
            } else if (current instanceof Map) {
                current = ((Map) current).get(subField);
            } else {
                try {
                    current = PropertyUtils.getProperty(current, subField);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new MotechException(e.getMessage(), e);
                }
            }
        }

        return current;
    }
}
