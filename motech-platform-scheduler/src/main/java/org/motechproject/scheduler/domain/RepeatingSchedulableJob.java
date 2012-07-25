package org.motechproject.scheduler.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.ObjectUtils;

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

    public RepeatingSchedulableJob(final MotechEvent motechEvent, final Date startTime, final Date endTime,
                                   final Integer repeatCount, final Long repeatIntervalInMilliSeconds) {
        this.motechEvent = motechEvent;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeatCount = repeatCount;
        this.repeatIntervalInMilliSeconds = repeatIntervalInMilliSeconds;
    }

    public RepeatingSchedulableJob(final MotechEvent motechEvent, final Date startTime, final Date endTime,
                                   final Long repeatIntervalInMilliSeconds) {
        this(motechEvent, startTime, endTime, null, repeatIntervalInMilliSeconds);
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

    @Override
    public String toString() {
        return "RepeatingSchedulableJob [motechEvent=" + motechEvent
                + ", startTime=" + startTime + ", endTime=" + endTime
                + ", repeatCount=" + repeatCount + ", repeatIntervalInMilliSeconds="
                + repeatIntervalInMilliSeconds + "]";
    }

    @Override
    public boolean equals(Object arg0) {
        if (!(arg0 instanceof RepeatingSchedulableJob)) {
            return false;
        }
        
        RepeatingSchedulableJob job = (RepeatingSchedulableJob)arg0;
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
        
        return hash;
    }

}
