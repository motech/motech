package org.motechproject.scheduler;

import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.SchedulableJob;

/**
 * Motech Scheduler Service Interface provides methods to schedule and unschedule a job
 *
 * User: Igor (iopushnyev@2paths.com)
 * Date: 16/02/11

 *
 */
public interface MotechSchedulerService {

    void scheduleJob(SchedulableJob schedulableJob);

    void scheduleRunOnceJob(RunOnceSchedulableJob schedulableJob);

    void unscheduleJob(String jobId);
}
