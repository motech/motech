package org.motechproject.scheduler.gateway;

import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.JobId;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;

/**
 * Motech Scheduler Gateway provides access to Motech Scheduler. A proxy for that interface will be generated at run-time.
 * <p/>
 * This interface should be injected into any class that needs access to Motech Scheduler for scheduling, unscheduling and
 * rescheduling jobs/tasks.
 * <p/>
 * The interface is configured in the schedulerOutboundChannelAdapter.xml (motech-platform-core)
 * <p/>
 * For example of use see SchedulerGatewayIT (motech-platform-core)
 *
 * @author Igor (iopushnyev@2paths.com)
 *         Date: 23/02/11
 */
public interface MotechSchedulerGateway {

    /**
     * Sends a message with the given SchedulableJob payload. The message directed to the channel specified in the
     * a Spring Integration configuration file.
     *
     * @param cronSchedulableJob
     */
    public void scheduleJob(CronSchedulableJob cronSchedulableJob);

    /**
     * Sends a message with the given SchedulableJob payload. The message directed to the channel specified in the
     * a Spring Integration configuration file.
     *
     * @param schedulableJob
     */
    public void scheduleRepeatingJob(RepeatingSchedulableJob schedulableJob);

    /**
     * Sends a message with the given RunOnceSchedulableJob payload. The message directed to the channel specified in the
     * a Spring Integration configuration file.
     *
     * @param schedulableJob
     */
    public void scheduleRunOnceJob(RunOnceSchedulableJob schedulableJob);

    /**
     * Sends a message with the given JobId payload. The message directed to the channel specified in the
     * a Spring Integration configuration file.
     *
     * @param job
     */
    public void unscheduleJob(JobId job);
}
