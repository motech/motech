package org.motechproject.scheduler.contract;

import org.apache.commons.lang.ObjectUtils;
import org.motechproject.event.MotechEvent;

import java.io.Serializable;
import java.util.Date;

/**
 * Schedulable Job - a data carrier class for a scheduled job that can be fired set number of times
 */
public class RepeatingSchedulableJob implements SchedulableJob, Serializable {
    private static final long serialVersionUID = 1L;

    private MotechEvent motechEvent;
    private Date startTime;
    private Date endTime;
    private Integer repeatCount;
    private Long repeatIntervalInMilliSeconds;
    private boolean ignorePastFiresAtStart;
    private boolean useOriginalFireTimeAfterMisfire;

    /**
     * Constructor. It will create a job, which will never end, won't ignore past fires at start and will use original fire time after misfire.
     * Start time, {@code MotechEvent}, repeat count and repeat interval are not assigned, which means that further usage, without setting them, can cause exceptions.
     */
    public RepeatingSchedulableJob() {
        endTime = null;
        ignorePastFiresAtStart = false;
        useOriginalFireTimeAfterMisfire = true;
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} which will be fired when the job triggers, not null
     * @param startTime  the {@code Date} at which job should become ACTIVE, not null
     * @param endTime  the {@code Date} at which job should be stopped, null treated as never end
     * @param repeatCount  the number of times job should be repeated, -1 treated as infinite
     * @param repeatIntervalInMilliSeconds  the interval(in milliseconds) between job fires
     * @param ignorePastFiresAtStart  the flag defining whether job should ignore past fires at start or not
     */
    public RepeatingSchedulableJob(final MotechEvent motechEvent, final Date startTime, final Date endTime, final Integer repeatCount, final Long repeatIntervalInMilliSeconds, boolean ignorePastFiresAtStart) {
        this.motechEvent = motechEvent;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeatCount = repeatCount;
        this.repeatIntervalInMilliSeconds = repeatIntervalInMilliSeconds;
        this.ignorePastFiresAtStart = ignorePastFiresAtStart;
        this.useOriginalFireTimeAfterMisfire = true;
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} which will be fired when the job triggers, not null
     * @param startTime  the {@code Date} at which job should become ACTIVE, not null
     * @param endTime  the {@code Date} at which job should be stopped, null treated as never end
     * @param repeatIntervalInMilliSeconds  the interval(in milliseconds) between job fires
     * @param ignorePastFiresAtStart  the flag defining whether job should ignore past fires at start or not
     */
    public RepeatingSchedulableJob(final MotechEvent motechEvent, final Date startTime, final Date endTime, final Long repeatIntervalInMilliSeconds, boolean ignorePastFiresAtStart) {
        this(motechEvent, startTime, endTime, null, repeatIntervalInMilliSeconds, ignorePastFiresAtStart);
    }

    public MotechEvent getMotechEvent() {
        return motechEvent;
    }

    public RepeatingSchedulableJob setMotechEvent(final MotechEvent motechEvent) {
        this.motechEvent = motechEvent;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public RepeatingSchedulableJob setStartTime(final Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public RepeatingSchedulableJob setEndTime(final Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public RepeatingSchedulableJob setRepeatCount(final Integer repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }

    public Long getRepeatIntervalInMilliSeconds() {
        return repeatIntervalInMilliSeconds;
    }

    public RepeatingSchedulableJob setRepeatIntervalInMilliSeconds(final Long repeatIntervalInMilliSeconds) {
        this.repeatIntervalInMilliSeconds = repeatIntervalInMilliSeconds;
        return this;
    }

    public boolean isIgnorePastFiresAtStart() {
        return ignorePastFiresAtStart;
    }

    /**
     * Ignore past fires when start time of job is in past.
     * <pre>ex : repeating job with interval of 5 unit, and current time in between fire 2 and 3 will start triggering from 3rd firetime.
     *  1     2     3     4
     *  +-----+-----+-----+
     *  start    ^current time
     *  </pre>
     * @param ignorePastFiresAtStart
     */
    public RepeatingSchedulableJob setIgnorePastFiresAtStart(boolean ignorePastFiresAtStart) {
        this.ignorePastFiresAtStart = ignorePastFiresAtStart;
        return this;
    }

    public boolean isUseOriginalFireTimeAfterMisfire() {
        return useOriginalFireTimeAfterMisfire;
    }

    public RepeatingSchedulableJob setUseOriginalFireTimeAfterMisfire(boolean useOriginalFireTimeAfterMisfire) {
        this.useOriginalFireTimeAfterMisfire = useOriginalFireTimeAfterMisfire;
        return this;
    }

    @Override
    public String toString() {
        return "RepeatingSchedulableJob [motechEvent=" + motechEvent
                + ", startTime=" + startTime + ", endTime=" + endTime
                + ", repeatCount=" + repeatCount + ", repeatIntervalInMilliSeconds="
                + repeatIntervalInMilliSeconds + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RepeatingSchedulableJob)) {
            return false;
        }
        RepeatingSchedulableJob job = (RepeatingSchedulableJob) obj;
        if (!ObjectUtils.equals(motechEvent, job.motechEvent)) {
            return false;
        } else if (!ObjectUtils.equals(startTime, job.startTime)) {
            return false;
        } else if (!ObjectUtils.equals(endTime, job.endTime)) {
            return false;
        } else if (!ObjectUtils.equals(repeatCount, job.repeatCount)) {
            return false;
        } else if (!ObjectUtils.equals(repeatIntervalInMilliSeconds, job.repeatIntervalInMilliSeconds)) {
            return false;
        } else if (ignorePastFiresAtStart != job.ignorePastFiresAtStart) {
            return false;
        } else if (useOriginalFireTimeAfterMisfire != job.useOriginalFireTimeAfterMisfire) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + ObjectUtils.hashCode(motechEvent);
        hash = hash * 31 + ObjectUtils.hashCode(startTime);
        hash = hash * 31 + ObjectUtils.hashCode(endTime);
        hash = hash * 31 + ObjectUtils.hashCode(repeatCount);
        hash = hash * 31 + ObjectUtils.hashCode(repeatIntervalInMilliSeconds);
        hash = hash * 31 + ObjectUtils.hashCode(ignorePastFiresAtStart);
        hash = hash * 31 + ObjectUtils.hashCode(useOriginalFireTimeAfterMisfire);

        return hash;
    }
}
