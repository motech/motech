package org.motechproject.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduler.factory.MotechSchedulerFactoryBean;
import org.motechproject.scheduler.service.impl.MotechScheduledJob;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;
import static org.springframework.test.util.ReflectionTestUtils.getField;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class SpringQuartzIT extends BasePaxIT {

    @Inject
    BundleContext context;

    MotechSchedulerFactoryBean factoryBean;

    String groupName = "group1";

    @Before
    public void setup() {
        factoryBean = (MotechSchedulerFactoryBean) getMotechSchedulerFactoryBean(context);
    }

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

        Scheduler scheduler = factoryBean.getQuartzScheduler();

        scheduler.scheduleJob(job, trigger);

        List<JobKey> jobKeys = new ArrayList<JobKey>(scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName)));
        List<String> jobNames = extractJobNames(jobKeys);

        assertEquals(1, jobNames.size());

        List<TriggerKey> triggerKeys = new ArrayList<TriggerKey>(scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(groupName)));
        List<String> triggerNames = extractTriggerNames(triggerKeys);
        assertEquals(asList(uuidStr), triggerNames);


        scheduler.unscheduleJob(triggerKey(uuidStr, groupName));
        scheduler.deleteJob(jobKey(uuidStr, groupName));

        jobKeys = new ArrayList<JobKey>(scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName)));
        jobNames = extractJobNames(jobKeys);
        assertEquals(0, jobNames.size());

        triggerKeys = new ArrayList<TriggerKey>(scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(groupName)));
        triggerNames = extractTriggerNames(triggerKeys);
        assertEquals(0, triggerNames.size());

    }


    @Test
    public void shouldWaitForJobsToCompleteBeforeShutdown() {
        MotechSchedulerFactoryBean factoryBean = (MotechSchedulerFactoryBean) getMotechSchedulerFactoryBean(context);
        assertTrue((Boolean) getField(factoryBean.getQuartzSchedulerFactoryBean(), "waitForJobsToCompleteOnShutdown"));
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
