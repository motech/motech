package org.motechproject.scheduler.contract;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;

/**
 * Run Once Schedulable Job - a data carrier class for a job scheduled in the future that can be fired only once
 * <p/>
 * This class is immutable
 * <p/>
 * User: Igor (iopushnyev@2paths.com)
 * Date: 16/02/11
 * Time: 1:43 PM
 */
public final class RunOnceSchedulableJob extends SchedulableJob {

    private static final long serialVersionUID = 1L;

    public RunOnceSchedulableJob() {
        super();
    }

    /**
     * Constructor
     *
     * @param motechEvent - event data message that will be send by Motech Scheduler when this job is fired
     * @param startDate   - date and time when the job fill be fired
     * @throws IllegalArgumentException if motechEvent or startDate is null or startDate is in past
     */
    public RunOnceSchedulableJob(MotechEvent motechEvent, DateTime startDate) {
        this(motechEvent, startDate, false);
    }

    /**
     * Constructor
     *
     * @param motechEvent - event data message that will be send by Motech Scheduler when this job is fired
     * @param startDate   - date and time when the job fill be fired
     * @throws IllegalArgumentException if motechEvent or startDate is null or startDate is in past
     * @param uiDefined  the flag defining, whether job has been created through the UI
     */
    public RunOnceSchedulableJob(MotechEvent motechEvent, DateTime startDate, boolean uiDefined) {
        super(motechEvent, startDate, uiDefined, false);
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

        return super.equals(that);
    }

    @Override
    public int hashCode() {
        int result = getMotechEvent().hashCode();
        result = 31 * result + getStartDate().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RunOnceSchedulableJob{" +
                "motechEvent=" + getMotechEvent() +
                ", startDate=" + getStartDate() +
                '}';
    }
}

