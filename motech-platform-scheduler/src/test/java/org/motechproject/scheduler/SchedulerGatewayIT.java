package org.motechproject.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.JobId;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.scheduler.gateway.MotechSchedulerGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Motech Scheduler integration test
 *
 * @author Igor (iopushnyev@2paths.com)
 *         Date: 23/02/11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testPlatformSchedulerApplicationContext.xml")
public class SchedulerGatewayIT {
    @Autowired
    private MotechSchedulerGateway motechSchedulerGateway;

    @Test
    public void testMotechScheduler() {
        Map<String, Object> cronParams = new HashMap<>();
        cronParams.put("JobID", "test_cron");

        Map<String, Object> runOnceParams = new HashMap<>();
        runOnceParams.put("JobID", "test_run_once");

        Map<String, Object> repeatingParams = new HashMap<>();
        repeatingParams.put("JobID", "test_repeating");

        MotechEvent cronEvent = new MotechEvent("cronTestEvent", cronParams);
        MotechEvent runOnceEvent = new MotechEvent("runOnceTestEvent", runOnceParams);
        MotechEvent repeatingEvent = new MotechEvent("repeatingTestEvent", repeatingParams);

        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(cronEvent, "0/2 * * * * ?");
        RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(runOnceEvent, new Date((new Date().getTime() + 5000)));
        RepeatingSchedulableJob repeatingSchedulableJob = new RepeatingSchedulableJob(repeatingEvent, new Date(), null, 4, 4 * 1000L);

        JobId cronJob = new JobId(cronEvent);

        motechSchedulerGateway.scheduleJob(cronSchedulableJob);

        motechSchedulerGateway.scheduleRunOnceJob(runOnceSchedulableJob);

        motechSchedulerGateway.scheduleRepeatingJob(repeatingSchedulableJob);

        motechSchedulerGateway.unscheduleJob(cronJob);
    }
}
