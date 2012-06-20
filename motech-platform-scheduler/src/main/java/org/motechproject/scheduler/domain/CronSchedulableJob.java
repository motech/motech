package org.motechproject.scheduler.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Schedulable Job - a data carrier class for a scheduled job that can be fired unlimited number of times
 * as specified with the cron expression
 *
 * @author Igor (iopushnyev@2paths.com)
 *         Date: 16/02/11
 *         Time: 1:43 PM
 */
public class CronSchedulableJob implements Serializable {

    private static final long serialVersionUID = 1L;

    private MotechEvent motechEvent;
    private String cronExpression;
    private Date startTime;
    private Date endTime;

    public CronSchedulableJob(MotechEvent motechEvent, String cronExpression,
                              Date startTime, Date endTime) {
        this(motechEvent, cronExpression);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public CronSchedulableJob(MotechEvent motechEvent, String cronExpression) {
        if (motechEvent == null) {
            throw new IllegalArgumentException("MotechEvent can not be null");
        }

        if (cronExpression == null || cronExpression.isEmpty()) {
            throw new IllegalArgumentException("Cron Expression can not be null or empty");
        }

        this.motechEvent = motechEvent;
        this.cronExpression = cronExpression;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public MotechEvent getMotechEvent() {
        return motechEvent;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((cronExpression == null) ? 0 : cronExpression.hashCode());
        result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = prime * result
                + ((motechEvent == null) ? 0 : motechEvent.hashCode());
        result = prime * result
                + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CronSchedulableJob other = (CronSchedulableJob) obj;
        if (cronExpression == null) {
            if (other.cronExpression != null)
                return false;
        } else if (!cronExpression.equals(other.cronExpression))
            return false;
        if (endTime == null) {
            if (other.endTime != null)
                return false;
        } else if (!endTime.equals(other.endTime))
            return false;
        if (motechEvent == null) {
            if (other.motechEvent != null)
                return false;
        } else if (!motechEvent.equals(other.motechEvent))
            return false;
        if (startTime == null) {
            if (other.startTime != null)
                return false;
        } else if (!startTime.equals(other.startTime))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SchedulableJob{" +
                "motechEvent=" + motechEvent +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", cronExpression='" + cronExpression + '\'' +
                '}';
    }
}
