package org.motechproject.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motech.scheduler.exception.MotechSchedulerException;
import org.motechproject.model.MotechScheduledEvent;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.SchedulableJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={"/testApplicationContext.xml"})

public class MotechSchedulerTest {

    @Autowired
    private MotechSchedulerService motechScheduler;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private String uuidStr = UUID.randomUUID().toString();

    @Test
    public void scheduleTest() throws Exception{

        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent(uuidStr, "testEvent", null);
        SchedulableJob schedulableJob = new SchedulableJob(scheduledEvent, "0 0 12 * * ?");

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.scheduleJob(schedulableJob);

        assertEquals(scheduledJobsNum+1, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }

    @Test(expected = MotechSchedulerException.class)
    public void scheduleInvalidCronExprTest() throws Exception{

        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent(uuidStr, null, null);
        SchedulableJob schedulableJob = new SchedulableJob(scheduledEvent, " ?");

        motechScheduler.scheduleJob(schedulableJob);

    }

    @Test(expected = IllegalArgumentException.class)
    public void scheduleNullJobTest() throws Exception{
        motechScheduler.scheduleJob(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void scheduleNullRuOnceJobTest() throws Exception{
        motechScheduler.scheduleRunOnceJob(null);
    }


    @Test
    public void updateScheduledJobHappyPathTest() throws Exception {

        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent(uuidStr, "testEvent", null);
        SchedulableJob schedulableJob = new SchedulableJob(scheduledEvent, "0 0 12 * * ?");

        motechScheduler.scheduleJob(schedulableJob);

        String patientIdKeyName = "patientId";
        String patientId = "1";
         HashMap<String, Object> params = new HashMap<String, Object>();
         params.put(patientIdKeyName, patientId);

        scheduledEvent = new MotechScheduledEvent(uuidStr, "testEvent", params);

        motechScheduler.updateScheduledJob(scheduledEvent);

        JobDataMap jobDataMap = schedulerFactoryBean.getScheduler().getJobDetail(uuidStr, MotechSchedulerServiceImpl.JOB_GROUP_NAME).getJobDataMap();

        assertEquals(patientId, jobDataMap.getString(patientIdKeyName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateScheduledJobNullTest() throws Exception {

        motechScheduler.updateScheduledJob(null);
    }


    @Test
    public void rescheduleJobHappyPathTest() throws Exception{

        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent(uuidStr, null, null);
        SchedulableJob schedulableJob = new SchedulableJob(scheduledEvent, "0 0 12 * * ?");

        motechScheduler.scheduleJob(schedulableJob);

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        String newCronExpression = "0 0 10 * * ?";

        motechScheduler.rescheduleJob(uuidStr, newCronExpression);
        assertEquals(scheduledJobsNum, schedulerFactoryBean.getScheduler().getTriggerNames( MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);

        CronTrigger trigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(uuidStr, MotechSchedulerServiceImpl.JOB_GROUP_NAME);
        String triggerCronExpression = trigger.getCronExpression();

        assertEquals(newCronExpression, triggerCronExpression);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rescheduleJobNullJobIdTest() {

        motechScheduler.rescheduleJob(null, "");

    }

    @Test(expected = IllegalArgumentException.class)
    public void rescheduleJobNullCronExpressionTest() {
        motechScheduler.rescheduleJob("", null);
    }

    @Test(expected = MotechSchedulerException.class)
    public void rescheduleJobInvalidCronExpressionTest() {

        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent(uuidStr, null, null);
        SchedulableJob schedulableJob = new SchedulableJob(scheduledEvent, "0 0 12 * * ?");

        motechScheduler.scheduleJob(schedulableJob);

        motechScheduler.rescheduleJob(uuidStr, "?");
    }

    @Test(expected = MotechSchedulerException.class)
    public void rescheduleNotExistingJobTest() {

        motechScheduler.rescheduleJob("0", "?");
    }


    @Test
    public void scheduleRunOnceJobTest() throws Exception{

        String uuidStr = UUID.randomUUID().toString();

        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent(uuidStr, "TestEvent", new HashMap<String, Object>());
        RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(scheduledEvent, new Date());

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.scheduleRunOnceJob(schedulableJob);

        assertEquals(scheduledJobsNum+1, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void scheduleRunOncePastJobTest() throws Exception{

        Calendar calendar =  Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent(uuidStr, null, null);

        RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(scheduledEvent, calendar.getTime());

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.scheduleRunOnceJob(schedulableJob);

        assertEquals(scheduledJobsNum+1, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }

    @Test
    public void unscheduleJobTest() throws Exception{

        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent(uuidStr, null, null);
        SchedulableJob schedulableJob = new SchedulableJob(scheduledEvent, "0 0 12 * * ?");

        motechScheduler.scheduleJob(schedulableJob);
        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.unscheduleJob(uuidStr);

        assertEquals(scheduledJobsNum - 1, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }

}
