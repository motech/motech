package org.motechproject.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testPlatformSchedulerApplicationContext.xml"})
public class SpringQuartzIT {

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	String groupName = "group1";


	@Test
	public void scheduleUnscheduleTest() throws Exception {

		String uuidStr = UUID.randomUUID().toString();

		JobDetail job = newJob(MotechScheduledJob.class)
				.withIdentity(jobKey(uuidStr, groupName))
				.build();
		job.getJobDataMap().put("eventType", "PillReminder");
		job.getJobDataMap().put("patientId", "001");

		Trigger trigger = newTrigger()
				.withIdentity(triggerKey(uuidStr, groupName))
				.startAt(new Date(new Date().getTime() + 3000))
				.build();

		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		scheduler.scheduleJob(job, trigger);

		scheduler = schedulerFactoryBean.getScheduler();

		Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
		assertEquals(1, jobKeys.size());

		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(groupName));
		assertEquals(1, triggerKeys.size());

		scheduler.unscheduleJob(triggerKey(uuidStr, groupName));
		scheduler.deleteJob(jobKey(uuidStr, groupName));

		jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
		assertEquals(0, jobKeys.size());

		triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(groupName));
		assertEquals(0, triggerKeys.size());

	}
}
