package org.motechproject.scheduler.constants;

/**
 * Contains constants used for securing parts of the scheduler module.
 */
public final class SchedulerConstants {

    public static final String VIEW_SCHEDULER_JOBS = "hasRole('viewSchedulerJobs')";
    public static final String EVENT_TYPE_KEY_NAME = "eventType";

    /* Job types */
    public static final String CRON = "CRON";
    public static final String DAY_OF_WEEK = "DAY_OF_WEEK";
    public static final String REPEATING = "REPEATING";
    public static final String REPEATING_PERIOD = "REPEATING_PERIOD";
    public static final String RUN_ONCE = "RUN_ONCE";

    /* Job parameters */
    public static final String END_DATE = "endDate";
    public static final String DAYS = "days";
    public static final String TIME = "time";
    public static final String UI_DEFINED = "uiDefined";
    public static final String CRON_EXPRESSION = "cronExpression";
    public static final String REPEAT_COUNT = "repeatCount";
    public static final String REPEAT_PERIOD = "repeatPeriod";
    public static final String REPEAT_INTERVAL_IN_SECONDS = "repeatIntervalInMilliseconds";
    public static final String IGNORE_PAST_FIRES_AT_START = "ignorePastFiresAtStart";
    public static final String USE_ORIGINAL_FIRE_TIME_AFTER_MISFIRE = "useOriginalFireTimeAfterMisfire";
    public static final String IS_DAY_OF_WEEK = "isDayOfWeek";

    private SchedulerConstants() {

    }
}
