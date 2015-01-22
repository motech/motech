package org.motechproject.scheduler.builder;

import org.motechproject.commons.date.model.Time;

/**
 * Builder for creating cron expressions for jobs, which should be triggered every {@code repeatIntervalInMinutes}
 * for {@code repeatWindowInHours} hours or stop at 23:{@code startTime.getMinute()}.
 */
public class CronJobExpressionBuilder {

    private Time startTime;
    private Integer repeatWindowInHours;
    private Integer repeatIntervalInMinutes;

    private static final String CRON_JOB_EXPR = "0 %d/%d %d-%d * * ?";

    /**
     * Constructor.
     *
     * @param startTime  the time at which job should become active, not null
     * @param repeatWindowInHours  the period(in hours) in which job should be active
     * @param repeatIntervalInMinutes  the interval between job fires
     */
    public CronJobExpressionBuilder(Time startTime, Integer repeatWindowInHours, Integer repeatIntervalInMinutes) {
        this.startTime = startTime;
        this.repeatWindowInHours = repeatWindowInHours;
        this.repeatIntervalInMinutes = repeatIntervalInMinutes;
    }

    /**
     * Builds cron expression with "0 M/M H-H + + ?" pattern.
     *
     * @return the cron expression ready to be used with CronSchedulableJob
     */
    public String build() {
        return String.format(CRON_JOB_EXPR, startTime.getMinute(), repeatIntervalInMinutes, startTime.getHour(), getEndHour());
    }

    private int getEndHour() {
        final int lastHourOfDay = 23;
        int currentEndHour = startTime.getHour() + repeatWindowInHours;
        return (currentEndHour > lastHourOfDay) ? lastHourOfDay : currentEndHour;
    }
}
