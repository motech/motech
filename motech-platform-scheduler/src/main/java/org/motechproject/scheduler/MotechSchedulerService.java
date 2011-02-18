package org.motechproject.scheduler;

import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.SchedulableJob;

/**
 * Created by IntelliJ IDEA.
 * User: az44
 * Date: 16/02/11
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MotechSchedulerService {
    void scheduleJob(SchedulableJob schedulableJob);

    void scheduleRunOnceJob(RunOnceSchedulableJob schedulableJob);

    void unscheduleJob(String jobId);
}
