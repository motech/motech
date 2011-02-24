package org.motechproject.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Schedulable Job - a data carrier class for a scheduled job that can be fired unlimited number of times
 *  as specified with the cron expression
 *
 * User: Igor (iopushnyev@2paths.com)
 * Date: 16/02/11
 * Time: 1:43 PM
 *
 */
public class SchedulableJob implements Serializable {

    private static final long serialVersionUID = 1L;

    private MotechScheduledEvent motechScheduledEvent;
    private String cronExpression;

    public SchedulableJob(MotechScheduledEvent motechScheduledEvent, String cronExpression) {

         if (motechScheduledEvent == null) {
            throw new IllegalArgumentException("MotechScheduledEvent can not be null");
        }

        if (cronExpression == null || cronExpression.isEmpty()) {
            throw new IllegalArgumentException("Cron Expression can not be null or empty");
        }

        this.motechScheduledEvent = motechScheduledEvent;
        this.cronExpression = cronExpression;
    }

    public MotechScheduledEvent getMotechScheduledEvent() {
        return motechScheduledEvent;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SchedulableJob that = (SchedulableJob) o;

        if (!cronExpression.equals(that.cronExpression)) return false;
        if (!motechScheduledEvent.equals(that.motechScheduledEvent)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = motechScheduledEvent.hashCode();
        result = 31 * result + cronExpression.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SchedulableJob{" +
                "motechScheduledEvent=" + motechScheduledEvent +
                ", cronExpression='" + cronExpression + '\'' +
                '}';
    }
}
