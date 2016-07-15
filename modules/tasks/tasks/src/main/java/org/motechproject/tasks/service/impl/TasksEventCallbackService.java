package org.motechproject.tasks.service.impl;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventCallbackService;
import org.motechproject.tasks.constants.EventDataKeys;
import org.motechproject.tasks.constants.TaskFailureCause;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public boolean failureCallback(MotechEvent event, Throwable throwable) {
        LOGGER.debug("Received failure callback for event subject {}", event.getSubject());

        Map<String, Object> metadata = event.getMetadata();
        Long activityId = (Long) metadata.get(EventDataKeys.TASK_ACTIVITY_ID);
        Task task = taskService.getTask((Long) metadata.get(EventDataKeys.TASK_ID));

        postExecutionHandler.handleError(event.getParameters(), metadata, task, new TaskHandlerException(TaskFailureCause.ACTION, "task.error.eventHandlerFailed", throwable), activityId);

        return false;
    }

    @Override
    public void successCallback(MotechEvent event) {
        LOGGER.debug("Received success callback for event subject {}", event.getSubject());
        Long activityId = (Long) event.getMetadata().get(EventDataKeys.TASK_ACTIVITY_ID);

        postExecutionHandler.handleActionExecuted(event.getParameters(), event.getMetadata(), activityId);
    }

    @Override
    public String getName() {
        return TASKS_EVENT_CALLBACK_NAME;
    }
}
