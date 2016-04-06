package org.motechproject.scheduler.builder;

import org.motechproject.commons.date.model.Time;

/**
 * Builder for simple cron expressions for jobs, which should be fired every {@code repeatIntervalInDays} days.
 */
public class CronJobSimpleExpressionBuilder {

    private Time startTime;

    private static final String CRON_JOB_EXPR = "0 %d %d %s * ?";
    private int dayOfMonth;

    /**
     * Constructor.
     *
     * @param startTime  the time at which job should become active, not null
     */
    public CronJobSimpleExpressionBuilder(Time startTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Argument can't be null!");
        }
        this.startTime = startTime;
    }

    /**
     * Sets interval on which job should be fired.
     *
     * @param repeatIntervalInDays  the interval(in days) between job fires, 0 means everyday
     * @return the {@code CronJobSimpleExpressionBuilder} ready to build cron expressions
     */
    public CronJobSimpleExpressionBuilder withRepeatIntervalInDays(int repeatIntervalInDays) {
        this.dayOfMonth = repeatIntervalInDays;
        return this;
    }

    /**
     * Builds cron expression with "0 M H D/D * ?" pattern for non-zero interval, or with "0 M H D * ?" pattern for zero interval.
     *
     * @return the cron expression ready to use with CronSchedulableJob
     */
    public String build() {
        String day = dayOfMonth == 0 ? "*" : "*/" + dayOfMonth;

        return String.format(CRON_JOB_EXPR, startTime.getMinute(), startTime.getHour(), day);
    }
}
