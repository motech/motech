package org.motechproject.scheduler;

import org.motechproject.model.MotechScheduledEvent;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.SchedulableJob;

/**
 *
 * User: az44
 * Date: 23/02/11
 * Time: 10:04 AM
 *
 */
public interface MotechSchedulerGateway {

 public void scheduleJob(SchedulableJob schedulableJob);

    public void updateScheduledJob(MotechScheduledEvent motechScheduledEvent);

    //public void rescheduleJob(String jobId, String cronExpression);

    public void scheduleRunOnceJob(RunOnceSchedulableJob schedulableJob);

    public void unscheduleJob(String jobId);
}
