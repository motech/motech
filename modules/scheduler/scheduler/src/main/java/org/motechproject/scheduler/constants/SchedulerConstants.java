package org.motechproject.scheduler.constants;

/**
 * Contains constants used for securing parts of the scheduler module.
 */
public final class SchedulerConstants {
    public static final String VIEW_SCHEDULER_JOBS = "hasRole('viewSchedulerJobs')";
    public static final String EVENT_TYPE_KEY_NAME = "eventType";

    private SchedulerConstants() {

    }
}
