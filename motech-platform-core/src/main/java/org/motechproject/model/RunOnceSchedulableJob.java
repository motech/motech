package org.motechproject.model;

import java.util.Date;

/**
 * Run Once Schedulable Job - a data carrier class for a job scheduled in the future that can be fired only once
 *
 * This class is immutable
 *
 * User: Igor (iopushnyev@2paths.com)
 * Date: 16/02/11
 * Time: 1:43 PM
 *
 */
public final class RunOnceSchedulableJob {

    private MotechScheduledEvent motechScheduledEvent;
    private Date startDate;

    /**
     * Constructor
     *
     * @param motechScheduledEvent - event data message that will be send by Motech Scheduler when this job is fired
     * @param startDate - date and time when the job fill be fired
     *
     * @throws IllegalArgumentException if motechScheduledEvent or startDate is null or startDate is in past
     */
    public RunOnceSchedulableJob(MotechScheduledEvent motechScheduledEvent, Date startDate) {

        if (motechScheduledEvent == null) {
            throw new IllegalArgumentException("MotechScheduledEvent can not be null");
        }

        if (startDate == null ) {
             throw new IllegalArgumentException("Start date can not be null");
        }
        Date currentDate = new Date();
        if (startDate.before(currentDate) ) {
             throw new IllegalArgumentException("Sstart date can not be in the past. \n" +
                                                " Start date: " + startDate.toString() +
                                                " now:" + currentDate.toString());
        }

        this.motechScheduledEvent = motechScheduledEvent;
        this.startDate = startDate;
    }

    public MotechScheduledEvent getMotechScheduledEvent() {
        return motechScheduledEvent;
    }

    public Date getStartDate() {
        return startDate;
    }
}

