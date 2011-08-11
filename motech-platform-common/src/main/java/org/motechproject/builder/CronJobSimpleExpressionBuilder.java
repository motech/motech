package org.motechproject.builder;

import org.motechproject.model.Time;

public class CronJobSimpleExpressionBuilder {

    private Time startTime;

    private final String CRON_JOB_EXPR = "0 %d %d * * ?";

    public CronJobSimpleExpressionBuilder(Time startTime) {
        this.startTime = startTime;
    }

    public String build() {
        return String.format(CRON_JOB_EXPR, startTime.getMinute(),  startTime.getHour());
    }
}
