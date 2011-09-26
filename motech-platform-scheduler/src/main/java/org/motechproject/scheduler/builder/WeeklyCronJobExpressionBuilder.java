package org.motechproject.scheduler.builder;


public class WeeklyCronJobExpressionBuilder {

    private final String CRON_JOB_EXPR = "0 0 0 ? * %d";
    
    int dayOfWeekFromJodaDate;
    public WeeklyCronJobExpressionBuilder(int dayOfWeekFromJodaDate	) {
    	this.dayOfWeekFromJodaDate = dayOfWeekFromJodaDate;
    }

    public String build() {
        return String.format(CRON_JOB_EXPR, ((dayOfWeekFromJodaDate+6) % 7)+1);
    }

}
