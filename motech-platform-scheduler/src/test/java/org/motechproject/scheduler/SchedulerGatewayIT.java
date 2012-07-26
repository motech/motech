package org.motechproject.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduler.domain.*;
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
    public void testMotechScheduler() throws Exception {
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
        RepeatingSchedulableJob repeatingSchedulableJob = new RepeatingSchedulableJob(repeatingEvent, new Date(), null, null, 4 * 1000L);

        JobId cronJob = new CronJobId(cronEvent);
        JobId repeatingJob = new RepeatingJobId(repeatingEvent);
        JobId runonceJob = new RunOnceJobId(runOnceEvent);

        motechSchedulerGateway.scheduleJob(cronSchedulableJob);

        motechSchedulerGateway.scheduleRunOnceJob(runOnceSchedulableJob);

        motechSchedulerGateway.scheduleRepeatingJob(repeatingSchedulableJob);

        Thread.sleep(8 * 1000L);

        motechSchedulerGateway.unscheduleJob(cronJob);

        motechSchedulerGateway.unscheduleJob(repeatingJob);
    }
}
