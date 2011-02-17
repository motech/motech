package org.motechproject.model;

import java.util.Date;

/**
 * Run Once Schedulable Job - a data carrier class for a job scheduled in the future that suppose to be fired only once
 * User: Igor (iopushnyev@2paths.com)
 * Date: 16/02/11
 * Time: 1:43 PM
 *
 */
public class RunOnceSchedulableJob {

    private MotechScheduledEvent motechScheduledEvent;
    private Date stsrtDate;

    /**
     *
     * @param motechScheduledEvent - event data message that will be send by Motech Scheduler when this job is fired
     * @param stsrtDate - date and time when the job fill be fired
     */
    public RunOnceSchedulableJob(MotechScheduledEvent motechScheduledEvent, Date stsrtDate) {
        this.motechScheduledEvent = motechScheduledEvent;
        this.stsrtDate = stsrtDate;
    }

    public MotechScheduledEvent getMotechScheduledEvent() {
        return motechScheduledEvent;
    }

    public Date getStsrtDate() {
        return stsrtDate;
    }
}

