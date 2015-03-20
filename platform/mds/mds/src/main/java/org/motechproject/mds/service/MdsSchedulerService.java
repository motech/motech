package org.motechproject.mds.service;

/**
 * The <code>MdsSchedulerService</code> provides methods for scheduling and unscheduling jobs. We do not
 * use the MOTECH scheduler, to avoid circular dependencies. This service only allows to schedule
 * MDS-specific jobs.
 */
public interface MdsSchedulerService {

    /**
     * Unschedules a job, responsible for periodic emptying of the MDS trash.
     */
    void unscheduleRepeatingJob();

    /**
     * Schedules a job, responsible for periodic emptying of the MDS trash. Throws {@link java.lang.IllegalArgumentException}
     * if the passed interval is set to 0.
     *
     * @param interval interval between next fires, in milliseconds
     */
    void scheduleRepeatingJob(long interval);
}
