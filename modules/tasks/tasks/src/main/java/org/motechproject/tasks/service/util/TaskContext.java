package org.motechproject.tasks.service.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.binary.StringUtils;
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
    private Map<String, Object> metadata;
    private TaskActivityService activityService;
    private Set<DataSourceObject> dataSourceObjects;
    private Set<PostActionParameterObject> postActionParameters;

    /**
     * Class constructor.
     *
     * @param task  the task, not null
     * @param parameters  the task parameters
     * @param activityService  the activity service, not null
     */
    public TaskContext(Task task, Map<String, Object> parameters, Map<String, Object> metadata, TaskActivityService activityService) {
        this.task = task;
        this.parameters = parameters;
        this.metadata = metadata;
        this.activityService = activityService;
        this.dataSourceObjects = new HashSet<>();
        this.postActionParameters = new HashSet<>();
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
     * Adds the given parameter to this task.
     *
     * @param objectId  the ID of the object, not null
     * @param objectKey  the Key of the object, not null
     * @param postActionParameter  the result of lookup execution, not null
     * @param failIfDataNotFound  defines whether task should fail if the data wasn't found
     */
    public void addPostActionParameterObject(String objectId, String objectKey, Object postActionParameter, boolean failIfDataNotFound) {
        Object objectValue = getFieldValue(postActionParameter, objectKey);

        postActionParameters.add(new PostActionParameterObject(objectId, objectKey, objectValue, failIfDataNotFound));
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
     * Returns the value of the post action parameter with the given key.
     *
     * @param key the key of the parameter, not null
     * @return the value of the parameter with the given key
     */
    public Object getPostActionParameterValue(String objectId, String key) throws TaskHandlerException {
        LOGGER.info("Retrieving task post action parameter with ID: {}", objectId);

        PostActionParameterObject postActionParameterObject = getPostActionParameter(objectId, key);
        if (postActionParameterObject == null) {
            throw new TaskHandlerException(TaskFailureCause.POST_ACTION_PARAMETER, "task.error.parameterNotFound", objectId);
        }

        try {
            return postActionParameterObject.getObjectValue();
        } catch (RuntimeException e) {
            LOGGER.warn("Parameter with id: {} not found", objectId);
            publishWarningActivity("task.error.parameterNotFound", objectId);
        }
        return null;
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
            if (!dataSourceObject.isNullWarningPublished()) {
                LOGGER.warn("Task data source object of type: {} not found", objectType);
                publishWarningActivity("task.warning.notFoundObjectForType", objectType);
                dataSourceObject.setNullWarningPublished(true);
            }
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

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    private DataSourceObject getDataSourceObject(String objectId) {
        for (DataSourceObject dataSourceObject : dataSourceObjects) {
            if (dataSourceObject.getObjectId().equals(objectId)) {
                return dataSourceObject;
            }
        }
        return null;
    }

    public Set<PostActionParameterObject> getPostActionParameters() {
        return postActionParameters;
    }

    private PostActionParameterObject getPostActionParameter(String objectId, String objectKey) {
        for (PostActionParameterObject postActionParameterObject : postActionParameters) {

            if (StringUtils.equals(postActionParameterObject.getObjectId(), objectId) &&
                    StringUtils.equals(postActionParameterObject.getObjectKey(), objectKey)) {
                return postActionParameterObject;
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
