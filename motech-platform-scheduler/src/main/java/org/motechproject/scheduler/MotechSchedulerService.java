package org.motechproject.scheduler;

import org.motechproject.model.MotechScheduledEvent;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.SchedulableJob;

/**
 * Motech Scheduler Service Interface provides methods to schedule reschedule and unschedule a job
 *
 * User: Igor (iopushnyev@2paths.com)
 * Date: 16/02/11

 *
 */
public interface MotechSchedulerService {

    public void scheduleJob(SchedulableJob schedulableJob);

    public void updateScheduledJob(MotechScheduledEvent motechScheduledEvent);

    public void rescheduleJob(String jobId, String cronExpression);

    public void scheduleRunOnceJob(RunOnceSchedulableJob schedulableJob);

    public void unscheduleJob(String jobId);
}
