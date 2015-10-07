package org.motechproject.tasks.domain;

/**
 * The <code>SchedulerJobType</code> enumerates possible types of jobs to be scheduled using
 * <code>MotechSchedulerService</code>.
 *
 *  @see org.motechproject.scheduler.service.MotechSchedulerService
 */
public enum SchedulerJobType {
    CRON_JOB,
    REPEATING_JOB,
    RUN_ONCE_JOB,
    DAY_OF_WEEK_JOB,
    REPEATING_JOB_WITH_PERIOD_INTERVAL
}
