package org.motechproject.scheduler.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Run Once Schedulable Job - a data carrier class for a job scheduled in the future that can be fired only once
 * <p/>
 * This class is immutable
 * <p/>
 * User: Igor (iopushnyev@2paths.com)
 * Date: 16/02/11
 * Time: 1:43 PM
 */
public final class RunOnceSchedulableJob implements Serializable {

    private static final long serialVersionUID = 1L;

    private MotechEvent motechEvent;
    private Date startDate;

    /**
     * Constructor
     *
     * @param motechEvent - event data message that will be send by Motech Scheduler when this job is fired
     * @param startDate   - date and time when the job fill be fired
     * @throws IllegalArgumentException if motechEvent or startDate is null or startDate is in past
     */
    public RunOnceSchedulableJob(MotechEvent motechEvent, Date startDate) {

        if (motechEvent == null) {
            throw new IllegalArgumentException("MotechEvent can not be null");
        }

        if (startDate == null) {
            throw new IllegalArgumentException("Start date can not be null");
        }

        this.motechEvent = motechEvent;
        this.startDate = startDate;
    }

    public MotechEvent getMotechEvent() {
        return motechEvent;
    }

    public Date getStartDate() {
        return startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RunOnceSchedulableJob that = (RunOnceSchedulableJob) o;

        if (!motechEvent.equals(that.motechEvent)) {
            return false;
        }
        if (!startDate.equals(that.startDate)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = motechEvent.hashCode();
        result = 31 * result + startDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RunOnceSchedulableJob{" +
                "motechEvent=" + motechEvent +
                ", startDate=" + startDate +
                '}';
    }
}

