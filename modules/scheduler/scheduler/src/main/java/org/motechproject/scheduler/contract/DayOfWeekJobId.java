package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;

public class DayOfWeekJobId extends JobId {

    private static final String SUFFIX_DAYOFWEEKJOBID = "";

    /**
     * Constructor.
     *
     * @param subject  the subject of {@code MotechEvent} fired, when job is triggered, not null
     * @param id  the "JobID" parameter for {@code MotechEvent} fired, when job is triggered, not null
     */
    public DayOfWeekJobId(String subject, String id) {
        super(subject, id, SUFFIX_DAYOFWEEKJOBID);
    }

    /**
     * Constructor.
     *
     * @param event  the {@code MotechEvent} fired, when job is triggered, not null
     */
    public DayOfWeekJobId(MotechEvent event) {
        super(event, SUFFIX_DAYOFWEEKJOBID);
    }
}