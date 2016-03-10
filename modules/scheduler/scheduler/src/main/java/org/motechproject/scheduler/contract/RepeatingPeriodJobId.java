package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;

/**
 * Jobs ID for RepeatingPeriodSchedulableJobs.
 */
public class RepeatingPeriodJobId extends JobId {

    public static final String SUFFIX_REPEATPERIODJOBID = "period";

    /**
     * Constructor.
     *
     * @param subject  the subject of {@code MotechEvent} fired, when job is triggered
     * @param id  the "JobID" parameter for {@code MotechEvent} fired, when job is triggered
     */
    public RepeatingPeriodJobId(String subject, String id) {
        super(subject, id, SUFFIX_REPEATPERIODJOBID);
    }

    /**
     * Constructor.
     *
     * @param repeatingEvent  the {@code MotechEvent} fired, when job is triggered
     */
    public RepeatingPeriodJobId(MotechEvent repeatingEvent) {
        super(repeatingEvent, SUFFIX_REPEATPERIODJOBID);
    }
}
