package org.motechproject.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testSchedulerApplicationContext.xml")
public class SpringQuartzIT {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    String groupName = "group1";


    @Test
    public void scheduleUnscheduleTest() throws SchedulerException {

        String uuidStr = UUID.randomUUID().toString();

        JobDetail job = newJob(MotechScheduledJob.class).withIdentity(uuidStr, groupName).build();
        job.getJobDataMap().put("eventType", "PillReminder");
        job.getJobDataMap().put("patientId", "001");

        SimpleTrigger trigger = newTrigger()
                .withIdentity(triggerKey(uuidStr, groupName))
                .withSchedule(simpleSchedule())
                .startAt(new Date(new Date().getTime() + 3000))
                .build();

        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        scheduler.scheduleJob(job, trigger);

        scheduler = schedulerFactoryBean.getScheduler();


        List<JobKey> jobKeys = new ArrayList<JobKey>(scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName)));
        List<String> jobNames = extractJobNames(jobKeys);

        assertEquals(1, jobNames.size());

        List<TriggerKey> triggerKeys = new ArrayList<TriggerKey>(scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(groupName)));
        List<String> triggerNames = extractTriggerNames(triggerKeys);
        assertEquals(1, triggerNames.size());


        scheduler.unscheduleJob(triggerKey(uuidStr, groupName));
        scheduler.deleteJob(jobKey(uuidStr, groupName));

        jobKeys = new ArrayList<JobKey>(scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName)));
        jobNames = extractJobNames(jobKeys);
        assertEquals(0, jobNames.size());

        triggerKeys = new ArrayList<TriggerKey>(scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(groupName)));
        triggerNames = extractTriggerNames(triggerKeys);
        assertEquals(0, triggerNames.size());

    }

    private List<String> extractJobNames(List<JobKey> jobKeys) {
        List<String> names = new ArrayList<String>();
        for (JobKey key : jobKeys)
            names.add(key.getName());
        return names;
    }

    private List<String> extractTriggerNames(List<TriggerKey> triggerKeys) {
        List<String> names = new ArrayList<String>();
        for (TriggerKey key : triggerKeys)
            names.add(key.getName());
        return names;
    }
}
