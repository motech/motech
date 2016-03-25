package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;

/**
 * Jobs ID for {@link DayOfWeekSchedulableJob}s.
 */
public class DayOfWeekJobId extends JobId {

    public static final String SUFFIX_DAY_OF_WEEK_JOB_ID = "-dayofweek";

    /**
     * Constructor.
     *
     * @param subject  the subject of {@code MotechEvent} fired, when job is triggered
     * @param id  the "JobID" parameter for {@code MotechEvent} fired, when job is triggered
     */
    public DayOfWeekJobId(String subject, String id) {
        super(subject, id, SUFFIX_DAY_OF_WEEK_JOB_ID);
    }

    /**
     * Constructor.
     *
     * @param repeatingEvent  the {@code MotechEvent} fired, when job is triggered
     */
    public DayOfWeekJobId(MotechEvent repeatingEvent) {
        super(repeatingEvent, SUFFIX_DAY_OF_WEEK_JOB_ID);
    }
}