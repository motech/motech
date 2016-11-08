package org.motechproject.tasks.service.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.SettingsFacade;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.motechproject.tasks.constants.EventDataKeys.HANDLER_ERROR_PARAM;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_FAILURE_DATE;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_FAILURE_NUMBER;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_MESSAGE;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_STACK_TRACE;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_TASK_ID;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_TASK_NAME;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_FAIL_TRIGGER_DISABLED;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_RETRY;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_RETRY_NUMBER;
import static org.motechproject.tasks.constants.EventSubjects.createHandlerFailureSubject;
import static org.motechproject.tasks.constants.EventSubjects.createHandlerSuccessSubject;

/**
 * This class is responsible for management of the tasks after their execution (both successfuland failed).
 * It manages task activities, disables task upon reaching error threshold and sends events informing about
 * success and failures of the tasks. The {@link TaskRetryHandler} is invoked from here to manage the Task
 * retries, baed on the task settings.
 */
@Component
public class TasksPostExecutionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TasksPostExecutionHandler.class);
    private static final String TASK_POSSIBLE_ERRORS_KEY = "task.possible.errors";
    private static final String TASK_PROPERTIES_FILE_NAME = "settings.properties";

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskActivityService activityService;

    @Autowired
    private EventRelay eventRelay;

    @Autowired
    private TaskRetryHandler retryHandler;

    @Autowired
    @Qualifier("tasksSettings")
    private SettingsFacade settings;

    /**
     * Handles successful execution of a single task action. If all actions of the task have been successfully executed,
     * it sends an event with the message about successful execution, resets the task failures in row count and passes the
     * info about successful execution to {@link TaskRetryHandler}.
     *
     * @param params trigger event parameters that invoked the task
     * @param activityId the id of an activity
     */
    public void handleActionExecuted(Map<String, Object> params, Map<String, Object> metadata, Long activityId) {
        boolean taskFinished = activityService.addSuccessfulExecution(activityId);
        if (taskFinished) {
            Long taskId = activityService.getTaskActivityById(activityId).getTask();
            Task task = taskService.getTask(taskId);

            handleSuccess(params, metadata, task);
        }
    }

    /**
     * Handles task action failure. It sets the specified task activity as failed and raises the failures in a row count of
     * a task. If the failure threshold is reached, it disables the task and publishes an event. It passes the
     * info about failed execution to {@link TaskRetryHandler}.
     *
     * @param params trigger event parameters that invoked the task
     * @param task the task that has failed
     * @param e the exception that caused the failure
     * @param activityId the id of an activity
     */
    public void handleError(Map<String, Object> params, Map<String, Object> metadata, Task task, TaskHandlerException e, Long activityId) {
        LOGGER.warn("Omitted task: {} with ID: {} because: {}", task.getName(), task.getId(), e);

        activityService.addFailedExecution(activityId, e);
        task.incrementFailuresInRow();

        LOGGER.info("The number of failures for task: {} is: {}", task.getName(), task.getFailuresInRow());

        int failureNumber = task.getFailuresInRow();
        int possibleErrorsNumber = getPossibleErrorsNumber();

        if (failureNumber >= possibleErrorsNumber) {
            task.setEnabled(false);

            activityService.addTaskDisabledWarning(task);
            publishTaskDisabledMessage(task.getName());
        }

        taskService.save(task);

        Map<String, Object> errorParam = new HashMap<>();
        errorParam.put(TASK_FAIL_MESSAGE, e.getMessage());
        errorParam.put(TASK_FAIL_STACK_TRACE, ExceptionUtils.getStackTrace(e));
        errorParam.put(TASK_FAIL_FAILURE_DATE, DateTime.now());
        errorParam.put(TASK_FAIL_FAILURE_NUMBER, failureNumber);
        errorParam.put(TASK_FAIL_TRIGGER_DISABLED, task.isEnabled());
        errorParam.put(TASK_FAIL_TASK_ID, task.getId());
        errorParam.put(TASK_FAIL_TASK_NAME, task.getName());

        Map<String, Object> errorEventParam = new HashMap<>();
        errorEventParam.putAll(params);
        errorEventParam.put(HANDLER_ERROR_PARAM, errorParam);

        eventRelay.sendEventMessage(new MotechEvent(
                createHandlerFailureSubject(task.getName(), e.getFailureCause()),
                errorEventParam
        ));

        getRetriesFromSettings(task, params, metadata);
        boolean retryScheduled = shouldScheduleRetry(metadata);

        retryHandler.handleTaskRetries(task, params, false, retryScheduled);
    }

    private void handleSuccess(Map<String, Object> params, Map<String, Object> metadata, Task task) {
        LOGGER.debug("All actions from task: {} with ID: {} were successfully executed", task.getName(), task.getId());

        task.resetFailuresInRow();
        taskService.save(task);

        eventRelay.sendEventMessage(new MotechEvent(
                createHandlerSuccessSubject(task.getName()),
                params
        ));

        boolean retryScheduled = shouldScheduleRetry(metadata);

        retryHandler.handleTaskRetries(task, params, true, retryScheduled);
    }

    private void publishTaskDisabledMessage(String taskName) {
        Map<String, Object> params = new HashMap<>();
        params.put("message", "Task disabled automatically: " + taskName);
        params.put("level", "CRITICAL");
        params.put("moduleName", settings.getBundleSymbolicName());

        eventRelay.sendEventMessage(new MotechEvent("org.motechproject.message", params));
    }

    private int getPossibleErrorsNumber() {
        String property = settings.getProperty(TASK_POSSIBLE_ERRORS_KEY);
        int number;

        try {
            number = Integer.parseInt(property);
        } catch (NumberFormatException e) {
            LOGGER.error(String.format(
                    "The value of key: %s is not a number. Possible errors number is set to zero.",
                    TASK_POSSIBLE_ERRORS_KEY
            ));
            number = 0;
        }

        return number;
    }

    private boolean shouldScheduleRetry(Map<String, Object> metadata) {
        return metadata.get(TASK_RETRY) != null && (boolean) metadata.get(TASK_RETRY);
    }


    private void getRetriesFromSettings(Task task, Map<String, Object> params, Map<String, Object> metadata) {
        List<Integer> retryInterval = new ArrayList<>();
        int numberOfRetries;

        try (InputStream retries = settings.getRawConfig(TASK_PROPERTIES_FILE_NAME)){
            if(retries != null) {
                Properties props = new Properties();
                props.load(retries);
                for (Map.Entry<Object, Object> entry : props.entrySet()) {
                    retryInterval.add(0, Integer.valueOf(entry.getValue().toString()));
                }
                numberOfRetries = retryInterval.size();
            } else {
                numberOfRetries = 0;
            }
        } catch (IOException e) {
            throw new MotechException("Error loading raw file config to properties", e);
        }

        setRetryParameters(task, metadata, params, retryInterval, numberOfRetries);
    }

    private void setRetryParameters(Task task,  Map<String, Object> metadata, Map<String, Object> params, List<Integer> retryInterval, int numberOfRetries)
    {
        int retryNumber = params.containsKey(TASK_RETRY_NUMBER) ? (Integer) params.get(TASK_RETRY_NUMBER) + 1 : 0;
        params.put(TASK_RETRY_NUMBER, retryNumber);

        if(retryNumber < numberOfRetries) {
            task.setRetryIntervalInMilliseconds(retryInterval.get(retryNumber) * 1000);
        } else {
            metadata.put(TASK_RETRY, true);
        }
    }

}
