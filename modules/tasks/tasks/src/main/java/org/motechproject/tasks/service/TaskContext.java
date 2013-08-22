package org.motechproject.tasks.service;

import org.apache.commons.lang.WordUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.events.constants.TaskFailureCause;
import org.motechproject.tasks.ex.TaskHandlerException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TaskContext holds task trigger event and data provider lookup objects that are used while executing filters/actions.
 */
public class TaskContext {

    private Task task;
    private MotechEvent event;
    private TaskActivityService activityService;
    private Set<DataSourceObject> dataSourceObjects;

    public TaskContext(Task task, MotechEvent event, TaskActivityService activityService) {
        this.task = task;
        this.event = event;
        this.activityService = activityService;
        this.dataSourceObjects = new HashSet<>();
    }

    public void addDataSourceObject(String objectId, Object dataSourceObject, boolean failIfDataNotFound) {
        dataSourceObjects.add(new DataSourceObject(objectId, dataSourceObject, failIfDataNotFound));
    }

    private DataSourceObject getDataSourceObject(String objectId) {
        for (DataSourceObject dataSourceObject : dataSourceObjects) {
            if (dataSourceObject.getObjectId().equals(objectId)) {
                return dataSourceObject;
            }
        }
        return null;
    }

    public Map<String, Object> getTriggerParameters() {
        return event.getParameters();
    }

    public Object getTriggerValue(String key) {
        Object value = null;

        if (event.getParameters() != null) {
            value = getFieldValue(event.getParameters(), key);
        }

        return value;
    }

    public Object getDataSourceObjectValue(String objectId, String field, String objectType) throws TaskHandlerException {
        DataSourceObject dataSourceObject = getDataSourceObject(objectId);
        if (dataSourceObject == null) {
            throw new TaskHandlerException(TaskFailureCause.DATA_SOURCE, "task.error.notFoundObjectForType", objectType);
        }

        if (dataSourceObject.getObjectValue() == null) {
            if (dataSourceObject.isFailIfNotFound()) {
                throw new TaskHandlerException(TaskFailureCause.DATA_SOURCE, "task.error.notFoundObjectForType", objectType);
            }
            publishWarningActivity("task.warning.notFoundObjectForType", objectType);
            return null;
        }

        try {
            return getFieldValue(dataSourceObject.getObjectValue(), field);
        } catch (Exception e) {
            if (dataSourceObject.isFailIfNotFound()) {
                throw new TaskHandlerException(TaskFailureCause.DATA_SOURCE, "task.error.objectNotContainsField", e, field);
            }
            publishWarningActivity("task.warning.objectNotContainsField", field);
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
                    Method method = current.getClass().getMethod("get" + WordUtils.capitalize(subField));
                    current = method.invoke(current);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new MotechException(e.getMessage(), e);
                }
            }
        }

        return current;
    }

    public void publishWarningActivity(String message, String field) {
        activityService.addWarning(task, message, field);
    }

    public Task getTask() {
        return task;
    }
}
