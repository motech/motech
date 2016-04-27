package org.motechproject.tasks.service.impl;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventCallbackService;
import org.motechproject.tasks.constants.EventDataKeys;
import org.motechproject.tasks.constants.TaskFailureCause;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link EventCallbackService} that allows to receive callbacks after the handler methods have
 * executed the events. This allows to handle task retries and keep track of the executions of the event-based task actions.
 */
@Service("tasksEventCallbackService")
public class TasksEventCallbackService implements EventCallbackService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TasksEventCallbackService.class);
    public static final String TASKS_EVENT_CALLBACK_NAME = "TasksEventCallback";

    @Autowired
    private TasksPostExecutionHandler postExecutionHandler;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskActivityService taskActivityService;

    @Override
    public boolean failureCallback(MotechEvent event, Throwable throwable) {
        LOGGER.debug("Received failure callback for event subject {}", event.getSubject());

        Map<String, Object> parameters = prepareParameters(event);
        Long activityId = (Long) parameters.get(EventDataKeys.TASK_ACTIVITY_ID);
        Task task = taskService.getTask((Long) parameters.get(EventDataKeys.TASK_ID));

        postExecutionHandler.handleError(parameters, task, new TaskHandlerException(TaskFailureCause.ACTION, "task.error.eventHandlerFailed", throwable), activityId);

        return false;
    }

    @Override
    public void successCallback(MotechEvent event) {
        LOGGER.debug("Received success callback for event subject {}", event.getSubject());

        Map<String, Object> parameters = prepareParameters(event);
        Long activityId = (Long) parameters.get(EventDataKeys.TASK_ACTIVITY_ID);

        postExecutionHandler.handleActionExecuted(parameters, activityId);
    }

    @Override
    public String getName() {
        return TASKS_EVENT_CALLBACK_NAME;
    }

    private Map<String, Object> prepareParameters(MotechEvent event) {
        Long activityId = (Long) event.getParameters().get(EventDataKeys.TASK_ACTIVITY_ID);
        TaskActivity activity = taskActivityService.getTaskActivityById(activityId);

        Map<String, Object> parameters = new HashMap<>();
        parameters.putAll(activity.getParameters());
        parameters.put(EventDataKeys.TASK_ID, activity.getTask());
        parameters.put(EventDataKeys.TASK_ACTIVITY_ID, activityId);
        parameters.put(EventDataKeys.TASK_RETRY, event.getParameters().get(EventDataKeys.TASK_RETRY));

        return parameters;
    }
}
