package org.motechproject.builder;

public class CronJobExpressionBuilder {

    private Integer startHour;
    private Integer startMinute;
    private Integer repeatWindowInHours;
    private Integer repeatIntervalInMinutes;

    private final String CRON_JOB_EXPR = "0 %d/%d %d-%d * * ?";

    public CronJobExpressionBuilder(Integer startHour, Integer startMinute, Integer repeatWindowInHours, Integer repeatIntervalInMinutes) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.repeatWindowInHours = repeatWindowInHours;
        this.repeatIntervalInMinutes = repeatIntervalInMinutes;
    }

    public String build() {
        return String.format(CRON_JOB_EXPR, startMinute, repeatIntervalInMinutes, startHour, getEndHour());
    }

    private int getEndHour() {
        int currentEndHour = startHour + repeatWindowInHours;
        return (currentEndHour > 23) ? 23 : currentEndHour;
    }
}
