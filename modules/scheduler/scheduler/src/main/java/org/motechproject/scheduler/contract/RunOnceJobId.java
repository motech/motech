package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;

/**
 * Represents ID for RunOnceSchedulableJob.
 */
public class RunOnceJobId extends JobId {

    public static final String SUFFIX_RUNONCEJOBID = "-runonce";

    /**
     * Constructor.
     *
     * @param subject  the subject of {@code MotechEvent} fired, when job is triggered
     * @param id  the "JobID" parameter for {@code MotechEvent} fired, when job is triggered
     */
    public RunOnceJobId(String subject, String id) {
        super(subject, id, SUFFIX_RUNONCEJOBID);
    }

    /**
     * Constructor.
     *
     * @param runOnceEvent  the {@code MotechEvent} fired, when job is triggered
     */
    public RunOnceJobId(MotechEvent runOnceEvent) {
        super(runOnceEvent, SUFFIX_RUNONCEJOBID);
    }
}
