package org.motechproject.scheduler.builder;


import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;

/**
 * Cron expression builder for jobs, which should be triggered on given day of week at given time.
 */
public class WeeklyCronJobExpressionBuilder {
    private int quartzDayOfWeek;
    private int hour;
    private int minute;

    /**
     * Constructor.
     *
     * @param dayOfWeek  the day of week at which job should be fired, not null
     */
    public WeeklyCronJobExpressionBuilder(DayOfWeek dayOfWeek) {
        quartzDayOfWeek = (dayOfWeek.getValue() % 7) + 1;
    }

    /**
     * Constructor.
     *
     * @param dayOfWeekNumber  the day of week at which job should be fired, must be in range from 1 to 7
     * @throws java.lang.IllegalArgumentException when dayOfWeekNumber isn't in range from 1 to 7
     */
    public WeeklyCronJobExpressionBuilder(int dayOfWeekNumber) {
        if (dayOfWeekNumber < 1 || dayOfWeekNumber > 7) {
            throw new IllegalArgumentException("Invalid argument! dayOfWeekNumber must be a number in range from 1 to 7");
        }
        quartzDayOfWeek = dayOfWeekNumber;
    }

    /**
     * Sets time, at which built job should be fired.
     *
     * @param time  the time at which job should be fired, not null
     * @return the {@code WeeklyCronJobExpressionBuilder} ready to build cron expressions
     */
    public WeeklyCronJobExpressionBuilder withTime(Time time) {
        this.hour = time.getHour();
        this.minute = time.getMinute();

        return this;
    }

    /**
     * Builds cron expression with "0 M H ? * D" pattern.
     *
     * @return the cron expression as String ready to be used with SchedulableJob classes
     */
    public String build() {
        final String cronExpression = "0 %d %d ? * %d";

        return String.format(cronExpression, minute, hour, quartzDayOfWeek);
    }
}
