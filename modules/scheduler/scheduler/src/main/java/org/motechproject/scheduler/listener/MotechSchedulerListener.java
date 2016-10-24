package org.motechproject.scheduler.listener;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.scheduler.contract.JobId;
import org.motechproject.scheduler.contract.RepeatingJobId;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * This component is used to schedule/unschedule jobs through the motech event.
 */
@Component
public class MotechSchedulerListener {

    private static final String SCHEDULE_REPEATING_JOB = "scheduleRepeatingJob";
    private static final String UNSCHEDULE_REPEATING_JOB = "unscheduleRepeatingJob";

    private static final String REPEAT_COUNT = "repeatCount";
    private static final String REPEAT_INTERVAL_TIME = "repeatIntervalInSeconds";
    private static final String JOB_SUBJECT = "jobSubject";
    private static final String JOB_START = "jobStart";

    private MotechSchedulerService schedulerService;

    /**
     * Handles the motech event scheduling a new repeating job.
     *
     * @param event the event to be handled
     */
    @MotechListener(subjects = {SCHEDULE_REPEATING_JOB})
    public void handleScheduleRepeatingJobEvent(MotechEvent event) {
        Map<String, Object> parameters = event.getParameters();
        Map<String, Object> metadata = event.getMetadata();

        String jobSubject = (String) metadata.get(JOB_SUBJECT);
        Integer repeatCount = (Integer) metadata.get(REPEAT_COUNT);
        Integer repeatIntervalInSeconds = (Integer) metadata.get(REPEAT_INTERVAL_TIME);
        Integer start = (Integer) metadata.get(JOB_START);
        MotechEvent jobEvent = new MotechEvent(jobSubject, parameters, null, metadata);

        RepeatingSchedulableJob repeatingJob = new RepeatingSchedulableJob(jobEvent, repeatCount, repeatIntervalInSeconds,
                DateTime.now().plusSeconds(start), null, false);

        schedulerService.scheduleRepeatingJob(repeatingJob);
    }

    /**
     * Handles the motech event unscheduling an existing repeating job.
     *
     * @param event the event to be handled
     */
    @MotechListener(subjects = {UNSCHEDULE_REPEATING_JOB})
    public void handleUnscheduleRepeatingJobEvent(MotechEvent event) {
        Map<String, Object> metadata = event.getMetadata();

        String jobSubject = (String) metadata.get(JOB_SUBJECT);

        MotechEvent jobEvent = new MotechEvent(jobSubject, null);

        JobId jobId = new RepeatingJobId(jobEvent);

        schedulerService.unscheduleJob(jobId);
    }

    @Autowired
    public void setSchedulerService(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}
