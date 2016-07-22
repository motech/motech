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

import static org.motechproject.tasks.constants.EventDataKeys.JOB_SUBJECT;
import static org.motechproject.tasks.constants.EventDataKeys.REPEAT_COUNT;
import static org.motechproject.tasks.constants.EventDataKeys.REPEAT_INTERVAL_TIME;
import static org.motechproject.tasks.constants.EventDataKeys.TASK_ID;
import static org.motechproject.tasks.constants.EventSubjects.SCHEDULE_REPEATING_JOB;
import static org.motechproject.tasks.constants.EventSubjects.UNSCHEDULE_REPEATING_JOB;

/**
 * This class is responsible for managing, scheduling and unscheduling jobs, connected to
 * repeating the task executions.
 */
@Component
public class TaskRetryHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRetryHandler.class);

    @Autowired
    private EventRelay eventRelay;

    /**
     * Takes necessary actions (schedule/unschedule) for the given task, based on its settings and status of the
     * execution. It makes sure that the retry job is scheduled only once and that it gets unscheduled when the task
     * executes successfully.
     *
     * @param task the task to handle repeat jobs for
     * @param parameters trigger event parameters
     * @param success whether the execution was successful
     * @param retryScheduled whether the tak retry is currently scheduled
     */
    public void handleTaskRetries(Task task, Map<String, Object> parameters, boolean success, boolean retryScheduled) {
        if (task.retryTaskOnFailure()) {
            if (success && retryScheduled) {
                LOGGER.info("Unscheduling the task retries, due to successful execution.");
                unscheduleTaskRetry(task.getTrigger().getEffectiveListenerRetrySubject());
            } else if (!success && !retryScheduled) {
                LOGGER.info("Scheduling task retries, since the execution of a task failed.");
                scheduleTaskRetry(task, parameters);
            }
        }
    }

    /**
     * Unschedules the task repeat job of the given subject.
     *
     * @param jobSubject the subject of a job to unschedule
     */
    public void unscheduleTaskRetry(String jobSubject) {
        LOGGER.info("Unscheduling the task retries.");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(JOB_SUBJECT, jobSubject);

        eventRelay.sendEventMessage(new MotechEvent(UNSCHEDULE_REPEATING_JOB, new HashMap<>(), null, metadata));
    }

    private void scheduleTaskRetry(Task task, Map<String, Object> parameters) {
        Map<String, Object> eventParameters = new HashMap<>();
        eventParameters.putAll(parameters);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(TASK_ID, task.getId());
        metadata.put(REPEAT_COUNT, task.getNumberOfRetries());
        metadata.put(REPEAT_INTERVAL_TIME, task.getRetryIntervalInMilliseconds() / 1000);
        metadata.put(JOB_SUBJECT, task.getTrigger().getEffectiveListenerRetrySubject());

        eventRelay.sendEventMessage(new MotechEvent(SCHEDULE_REPEATING_JOB, eventParameters, null, metadata));
    }
}
