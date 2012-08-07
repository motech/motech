package org.motechproject.scheduler.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Schedulable Job - a data carrier class for a scheduled job that can be fired set number of times
 */
public class RepeatingSchedulableJob implements Serializable {
    private static final long serialVersionUID = 1L;

    private MotechEvent motechEvent;
    private Date startTime;
    private Date endTime;
    private Integer repeatCount;
    private Long repeatIntervalInMilliSeconds;
    private boolean ignorePastFiresAtStart;
    private boolean useOriginalFireTimeAfterMisfire = false;

    public RepeatingSchedulableJob(final MotechEvent motechEvent, final Date startTime, final Date endTime, final Integer repeatCount, final Long repeatIntervalInMilliSeconds, boolean ignorePastFiresAtStart) {
        this.motechEvent = motechEvent;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeatCount = repeatCount;
        this.repeatIntervalInMilliSeconds = repeatIntervalInMilliSeconds;
        this.ignorePastFiresAtStart = ignorePastFiresAtStart;
    }

    public RepeatingSchedulableJob(final MotechEvent motechEvent, final Date startTime, final Date endTime, final Long repeatIntervalInMilliSeconds, boolean ignorePastFiresAtStart) {
        this(motechEvent, startTime, endTime, null, repeatIntervalInMilliSeconds, ignorePastFiresAtStart);
    }

    public MotechEvent getMotechEvent() {
        return motechEvent;
    }

    public void setMotechEvent(final MotechEvent motechEvent) {
        this.motechEvent = motechEvent;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(final Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Long getRepeatIntervalInMilliSeconds() {
        return repeatIntervalInMilliSeconds;
    }

    public void setRepeatIntervalInMilliSeconds(final Long repeatIntervalInMilliSeconds) {
        this.repeatIntervalInMilliSeconds = repeatIntervalInMilliSeconds;
    }

    public boolean isIgnorePastFiresAtStart() {
        return ignorePastFiresAtStart;
    }

    /**
     * Ignore past fires when start time of job is in past.
     * <pre>ex : repeating job with interval of 5 unit, and current time in between fire 2 and 3 will start triggering from 3rd firetime.
     *  1     2     3     4
     *  |-----|-----|-----|
     *  start    ^current time
     *  </pre>
     * @param ignorePastFiresAtStart
     */
    public void setIgnorePastFiresAtStart(boolean ignorePastFiresAtStart) {
        this.ignorePastFiresAtStart = ignorePastFiresAtStart;
    }

    public boolean isUseOriginalFireTimeAfterMisfire() {
        return useOriginalFireTimeAfterMisfire;
    }

    public void setUseOriginalFireTimeAfterMisfire(boolean useOriginalFireTimeAfterMisfire) {
        this.useOriginalFireTimeAfterMisfire = useOriginalFireTimeAfterMisfire;
    }

    @Override
    public String toString() {
        return "RepeatingSchedulableJob [motechEvent=" + motechEvent
                + ", startTime=" + startTime + ", endTime=" + endTime
                + ", repeatCount=" + repeatCount + ", repeatIntervalInMilliSeconds="
                + repeatIntervalInMilliSeconds + "]";
    }

}
