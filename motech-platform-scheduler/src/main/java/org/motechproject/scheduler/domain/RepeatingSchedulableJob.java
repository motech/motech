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
    private long repeatIntervalInMilliSeconds;

    public RepeatingSchedulableJob(MotechEvent motechEvent, Date startTime, Date endTime,
                                   Integer repeatCount, long repeatIntervalInMilliSeconds) {      // TODO: have consistentcy for using primitives/objects
        this.motechEvent = motechEvent;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeatCount = repeatCount;
        this.repeatIntervalInMilliSeconds = repeatIntervalInMilliSeconds;
    }

    public RepeatingSchedulableJob(MotechEvent motechEvent, Date startTime, Date endTime,
                                   long repeatIntervalInMilliSeconds) {
        this(motechEvent, startTime, endTime, null, repeatIntervalInMilliSeconds);
    }

    public MotechEvent getMotechEvent() {
        return motechEvent;
    }

    public void setMotechEvent(MotechEvent motechEvent) {
        this.motechEvent = motechEvent;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public long getRepeatIntervalInMilliSeconds() {
        return repeatIntervalInMilliSeconds;
    }

    public void setRepeatIntervalInMilliSeconds(long repeatIntervalInMilliSeconds) {
        this.repeatIntervalInMilliSeconds = repeatIntervalInMilliSeconds;
    }

    @Override
    public String toString() {
        return "RepeatingSchedulableJob [motechEvent=" + motechEvent
                + ", startTime=" + startTime + ", endTime=" + endTime
                + ", repeatCount=" + repeatCount + ", repeatIntervalInMilliSeconds="
                + repeatIntervalInMilliSeconds + "]";
    }

}
