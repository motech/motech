package org.motechproject.tasks.service.impl;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.domain.mds.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.tasks.constants.EventDataKeys.JOB_START;
import static org.motechproject.tasks.constants.EventDataKeys.JOB_SUBJECT;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_ID;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_RETRY_NUMBER;
import static org.motechproject.tasks.constants.EventSubjects.SCHEDULE_REPEATING_JOB;

/**
 * This class is responsible for managing and scheduling jobs, connected to
 * repeating the task executions.
 */
@Component
public class TaskRetryHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRetryHandler.class);

    @Autowired
    private EventRelay eventRelay;

    /**
     * Takes schedule action for the given task, based on its settings and status of the
     * execution.
     *
     * @param task the task to handle repeat jobs for
     * @param parameters trigger event parameters
     */
    public void handleTaskRetries(Task task, Map<String, Object> parameters) {
        if (task.isRetryTaskOnFailure()) {
            LOGGER.info("Scheduling task retries, since the execution of a task failed.");
            scheduleTaskRetry(task, parameters);
        }
    }

    private void scheduleTaskRetry(Task task, Map<String, Object> parameters) {
        Map<String, Object> eventParameters = new HashMap<>();
        eventParameters.putAll(parameters);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(TASK_ID, task.getId());
        metadata.put(JOB_START, task.getRetryIntervalInMilliseconds() / 1000);
        metadata.put(JOB_SUBJECT, task.getTrigger().getEffectiveListenerRetrySubject());
        metadata.put(TASK_RETRY_NUMBER, parameters.get(TASK_RETRY_NUMBER));

        eventRelay.sendEventMessage(new MotechEvent(SCHEDULE_REPEATING_JOB, eventParameters, null, metadata));
    }
}
