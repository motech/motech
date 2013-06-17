package org.motechproject.scheduler.quartz;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.scheduler.factory.MotechSchedulerFactoryBean;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.testing.utils.IdGenerator.id;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:testCouchDbSchedulerApplicationContext.xml")
public class CouchDbStoreSchedulerIT {

    @Autowired
    MotechSchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    EventListenerRegistryService eventListenerRegistry;

    Scheduler scheduler;

    @Before
    public void setUp() throws Exception {
        scheduler = schedulerFactoryBean.getQuartzScheduler();
    }

    @Test
    public void shouldScheduleAndFireSimpleTrigger() throws SchedulerException, InterruptedException {
        scheduler.clear();

        int totalEvents = 3;

        String jobId = id("fooid");
        JobDetail job = newJob(JobListener.class)
            .withIdentity(jobId, "bargroup")
            .usingJobData("foo", "bar")
            .usingJobData("fuu", "baz")
            .build();

        String triggerId = id("fuuid1");
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity(triggerId, "borgroup1")
            .forJob(JobKey.jobKey(jobId, "bargroup"))
            .startAt(now().toDate())
            .withSchedule(simpleSchedule()
                    .withIntervalInSeconds(5)
                    .withRepeatCount(totalEvents - 1))
            .build();

        scheduler.scheduleJob(job, trigger);

        final List<Date> fireTimes = JobListener.getFireTimes();
        synchronized (fireTimes) {
            while (fireTimes.size() < totalEvents) {
                fireTimes.wait(5000);
            }
        }
        assertEquals(totalEvents, fireTimes.size());
    }
}
