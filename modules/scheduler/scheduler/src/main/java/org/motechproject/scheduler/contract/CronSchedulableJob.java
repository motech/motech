package org.motechproject.scheduler.contract;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;

import java.util.Objects;

/**
 * Schedulable Job - a data carrier class for a scheduled job that can be fired unlimited number of times
 * as specified with the cron expression
 *
 * @author Igor (iopushnyev@2paths.com)
 *         Date: 16/02/11
 *         Time: 1:43 PM
 */
public class CronSchedulableJob extends SchedulableJob {

    private static final long serialVersionUID = 1L;

    private MotechEvent motechEvent;
    private String cronExpression;
    private DateTime startTime;
    private DateTime endTime;
    private boolean ignorePastFiresAtStart;

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} fired, when job triggers, not null
     * @param cronExpression  the cron expression, which defines when job should be fired, not null
     * @param startTime  the {@code DateTime} at which job should become ACTIVE, not null
     * @param endTime  the {@code DateTime} at which job should be stopped, null treated as never end
     */
    public CronSchedulableJob(MotechEvent motechEvent, String cronExpression, DateTime startTime, DateTime endTime) {
        this(motechEvent, cronExpression, startTime, endTime, false);
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} fired, when job triggers, not null
     * @param cronExpression  the cron expression, which defines when job should be fired, not null
     */
    public CronSchedulableJob(MotechEvent motechEvent, String cronExpression) {
        this(motechEvent, cronExpression, null, null, false);
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} fired, when job triggers, not null
     * @param cronExpression  the cron expression, which defines when job should be fired, not null
     * @param startTime  the {@code DateTime} at which job should become ACTIVE, not null
     * @param endTime  the {@code DateTime} at which job should be stopped, null treated as never end
     * @param ignorePastFiresAtStart  the flag defining, whether job should ignore past fires at start or not
     */
    public CronSchedulableJob(MotechEvent motechEvent, String cronExpression, DateTime startTime, DateTime endTime, boolean ignorePastFiresAtStart) {
        if (motechEvent == null) {
            throw new IllegalArgumentException("MotechEvent can not be null");
        }

        if (cronExpression == null || cronExpression.isEmpty()) {
            throw new IllegalArgumentException("Cron Expression can not be null or empty");
        }
        this.motechEvent = motechEvent;
        this.cronExpression = cronExpression;
        this.startTime = startTime;
        this.endTime = endTime;
        this.ignorePastFiresAtStart = ignorePastFiresAtStart;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public MotechEvent getMotechEvent() {
        return motechEvent;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public boolean isIgnorePastFiresAtStart() {
        return ignorePastFiresAtStart;
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
        result = prime * result + (ignorePastFiresAtStart ? 0 : 1);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CronSchedulableJob)) {
            return false;
        }

        CronSchedulableJob other = (CronSchedulableJob) obj;

        return
            Objects.equals(motechEvent, other.motechEvent) &&
            Objects.equals(cronExpression, other.cronExpression) &&
            Objects.equals(startTime, other.startTime) &&
            Objects.equals(endTime, other.endTime) &&
            Objects.equals(ignorePastFiresAtStart, other.ignorePastFiresAtStart);
    }

    @Override
    public String toString() {
        return "SchedulableJob{" +
                "motechEvent=" + motechEvent +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", cronExpression='" + cronExpression + '\'' +
                ", ignorePastFiresAtStart='" + ignorePastFiresAtStart + '\'' +
                '}';
    }
}
