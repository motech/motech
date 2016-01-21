package org.motechproject.scheduler.service;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.scheduler.contract.JobId;
import org.motechproject.scheduler.contract.RepeatingJobId;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * This service is used to schedule/unschedule jobs through the motech event.
 */
@Service
public class MotechSchedulerListener {

    private static final String SCHEDULE_JOB = "scheduleJob";
    private static final String UNSCHEDULE_JOB = "unscheduleJob";

    private static final String REPEAT_COUNT = "repeatCount";
    private static final String REPEAT_INTERVAL_TIME = "repeatIntervalInSeconds";
    private static final String JOB_SUBJECT = "jobSubject";

    private MotechSchedulerService schedulerService;

    /**
     * Handles the motech event scheduling new job.
     *
     * @param event the event to be handled
     */
    @MotechListener(subjects = { SCHEDULE_JOB })
    public void handleScheduleJobEvent(MotechEvent event) {
        Map<String, Object> parameters = event.getParameters();
        String jobSubject = (String) parameters.get(JOB_SUBJECT);
        MotechEvent jobEvent = new MotechEvent(jobSubject, parameters);

        Integer repeatCount = (Integer) parameters.get(REPEAT_COUNT);
        Integer repeatIntervalInSeconds = (Integer) parameters.get(REPEAT_INTERVAL_TIME);

        RepeatingSchedulableJob repeatingJob = new RepeatingSchedulableJob(jobEvent, repeatCount, repeatIntervalInSeconds, DateTime.now().toDate(), null, false);

        schedulerService.scheduleRepeatingJob(repeatingJob);
    }

    /**
     * Handles the motech event unscheduling existing job.
     *
     * @param event the event to be handled
     */
    @MotechListener(subjects = { UNSCHEDULE_JOB })
    public void handleUnscheduleJobEvent(MotechEvent event) {
        Map<String, Object> parameters = event.getParameters();
        String jobSubject = (String) parameters.get(JOB_SUBJECT);
        MotechEvent jobEvent = new MotechEvent(jobSubject, null);

        JobId jobId = new RepeatingJobId(jobEvent);

        schedulerService.unscheduleJob(jobId);
    }

    @Autowired
    public void setSchedulerService(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}
