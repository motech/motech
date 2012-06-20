package org.motechproject.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.MotechEvent;
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
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", "test_1");
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0/5 0 * * * ?");

        motechSchedulerGateway.scheduleJob(cronSchedulableJob);

        motechSchedulerGateway.unscheduleJob("test_1");

        RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, new Date((new Date().getTime() + 5000)));

        motechSchedulerGateway.scheduleRunOnceJob(runOnceSchedulableJob);
    }
}
