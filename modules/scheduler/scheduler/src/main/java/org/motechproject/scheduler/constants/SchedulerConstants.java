package org.motechproject.scheduler.constants;

/**
 * Contains constants used for securing parts of the scheduler module.
 */
public final class SchedulerConstants {

    public static final String VIEW_SCHEDULER_JOBS = "hasRole('viewSchedulerJobs')";
    public static final String EVENT_TYPE_KEY_NAME = "eventType";

    /* Job parameters */
    public static final String END_DATE = "endDate";
    public static final String DAYS = "days";
    public static final String TIME = "time";
    public static final String CRON_EXPRESSION = "cronExpression";
    public static final String REPEAT_COUNT = "repeatCount";
    public static final String REPEAT_PERIOD = "repeatPeriod";
    public static final String REPEAT_INTERVAL_IN_SECONDS = "repeatIntervalInMilliseconds";
    public static final String IGNORE_PAST_FIRES_AT_START = "ignorePastFiresAtStart";
    public static final String USE_ORIGINAL_FIRE_TIME_AFTER_MISFIRE = "useOriginalFireTimeAfterMisfire";

    private SchedulerConstants() {

    }
}
