package org.motechproject.model;

import java.util.HashMap;

/**
 * Schedulable Job - a data carrier class for a scheduled job that can be fired unlimited number of times
 *  as specified with the cron expression
 * User: Igor (iopushnyev@2paths.com)
 * Date: 16/02/11
 * Time: 1:43 PM
 *
 */
public class SchedulableJob {

    private MotechScheduledEvent motechScheduledEvent;
    private String cronExpression;

    public SchedulableJob(MotechScheduledEvent motechScheduledEvent, String cronExpression) {
        this.motechScheduledEvent = motechScheduledEvent;
        this.cronExpression = cronExpression;
    }

    public MotechScheduledEvent getMotechScheduledEvent() {
        return motechScheduledEvent;
    }

    public String getCronExpression() {
        return cronExpression;
    }
}
