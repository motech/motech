package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;

/**
 * Represents ID for CronSchedulableJobs.
 */
public class CronJobId extends JobId {

    public static final String SUFFIX_CRON_JOB_ID = "cron";

    /**
     * Constructor.
     *
     * @param subject  the subject of {@code MotechEvent} fired, when job is triggered, not null
     * @param id  the "JobID" parameter for {@code MotechEvent} fired, when job is triggered, not null
     */
    public CronJobId(String subject, String id) {
        super(subject, id, SUFFIX_CRON_JOB_ID);
    }

    /**
     * Constructor.
     *
     * @param event  the {@code MotechEvent} fired, when job is triggered, not null
     */
    public CronJobId(MotechEvent event) {
        super(event, SUFFIX_CRON_JOB_ID);
    }
}
