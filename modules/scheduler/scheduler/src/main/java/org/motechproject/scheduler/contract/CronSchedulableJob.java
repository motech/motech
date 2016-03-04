package org.motechproject.scheduler.contract;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;

import java.util.Objects;

/**
 * Schedulable Job - a data carrier class for a scheduled job that can be fired unlimited number of times
 * as specified with the cron expression
 *
 */
public class CronSchedulableJob extends EndingSchedulableJob {

    private static final long serialVersionUID = 1L;

    private String cronExpression;

    public CronSchedulableJob() {
        this(null, null, null, null, false, false);
    }

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
        this(motechEvent, cronExpression, startTime, endTime, ignorePastFiresAtStart, false);
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} fired, when job triggers, not null
     * @param cronExpression  the cron expression, which defines when job should be fired, not null
     * @param startTime  the {@code Date} at which job should become ACTIVE, not null
     * @param endTime  the {@code Date} at which job should be stopped, null treated as never end
     * @param ignorePastFiresAtStart  the flag defining, whether job should ignore past fires at start or not
     * @param uiDefined  the flag defining, whether job has been created through the UI
     */
    public CronSchedulableJob(MotechEvent motechEvent, String cronExpression, DateTime startTime, DateTime endTime,
                              boolean ignorePastFiresAtStart, boolean uiDefined) {
        super(motechEvent, startTime, endTime, uiDefined, ignorePastFiresAtStart);
        this.cronExpression = cronExpression;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((cronExpression == null) ? 0 : cronExpression.hashCode());
        result = prime * result + ((getEndDate() == null) ? 0 : getEndDate().hashCode());
        result = prime * result
                + ((getMotechEvent() == null) ? 0 : getMotechEvent().hashCode());
        result = prime * result
                + ((getStartDate() == null) ? 0 : getStartDate().hashCode());
        result = prime * result + (isIgnorePastFiresAtStart() ? 0 : 1);
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

        return Objects.equals(cronExpression, other.cronExpression)
                && super.equals(other);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("SchedulableJob{motechEvent=").append(getMotechEvent())
                .append(", startTime=").append(getStartDate())
                .append(", endTime=").append(getEndDate())
                .append(", cronExpression='").append(cronExpression)
                .append("', ignorePastFiresAtStart='").append(isIgnorePastFiresAtStart()).append("'}");

        return builder.toString();
    }
}
