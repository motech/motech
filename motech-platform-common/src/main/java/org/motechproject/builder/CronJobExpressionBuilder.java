package org.motechproject.builder;

public class CronJobExpressionBuilder {

    private Integer startHour;
    private Integer startMinute;
    private Integer repeatCount;
    private Integer repeatIntervalInMinutes;

    private final String NON_REPEATING_CRON_JOB_EXPR = "0 %d %d * * ?";
    private final String REPEATING_CRON_JOB_EXPR = "0 %d/%d %d-%d * * ?";

    public CronJobExpressionBuilder(Integer startHour, Integer startMinute, Integer repeatCount, Integer repeatIntervalInMinutes) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.repeatCount = repeatCount;
        this.repeatIntervalInMinutes = repeatIntervalInMinutes;
    }

    public String build() {
        if (repeatCount == 0){
            return String.format(NON_REPEATING_CRON_JOB_EXPR, startMinute, startHour);
        }
        return String.format(REPEATING_CRON_JOB_EXPR, startMinute, repeatIntervalInMinutes, startHour, getEndHour());
    }

    private long getEndHour() {
        double endHourInMinutes = (startHour * 60) + startMinute + (repeatCount * repeatIntervalInMinutes);
        return (long) Math.ceil(endHourInMinutes / 60);
    }
}
