package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;

/**
 * Jobs ID for RepeatingSchedulableJobs.
 */
public class RepeatingJobId extends JobId {

    public static final String SUFFIX_REPEATJOBID = "-repeat";

    /**
     * Constructor.
     *
     * @param subject  the subject of {@code MotechEvent} fired, when job is triggered
     * @param id  the "JobID" parameter for {@code MotechEvent}, when job is triggered
     */
    public RepeatingJobId(String subject, String id) {
        super(subject, id, SUFFIX_REPEATJOBID);
    }

    /**
     * Constructor.
     *
     * @param repeatingEvent  the {@code MotechEvent} fired, when job is triggered
     */
    public RepeatingJobId(MotechEvent repeatingEvent) {
        super(repeatingEvent, SUFFIX_REPEATJOBID);
    }
}
