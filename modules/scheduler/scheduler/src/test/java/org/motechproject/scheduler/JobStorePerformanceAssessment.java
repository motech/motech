package org.motechproject.scheduler;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.NanoStopWatch;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.quartz.CouchDbJobStoreException;
import org.motechproject.quartz.CouchDbStore;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.scheduler.factory.MotechSchedulerFactoryBean;
import org.motechproject.scheduler.impl.MotechSchedulerServiceImpl;
import org.motechproject.server.config.SettingsFacade;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.motechproject.commons.date.util.DateUtil.now;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/performanceTestSchedulerApplicationContext.xml"})
public class JobStorePerformanceAssessment {

    @Autowired
    EventListenerRegistryService eventListenerRegistryService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    @Qualifier("couchdbSchedulerSettings")
    SettingsFacade couchdbSettingsFacade;

    @Autowired
    @Qualifier("jdbcSchedulerSettings")
    SettingsFacade jdbcSettingsFacade;

    MotechSchedulerFactoryBean couchSchedulerFactoryBean;
    MotechSchedulerFactoryBean jdbcSchedulerFactoryBean;

    CouchDbStore couchdbStore;

    @Before
    public void setup() throws IOException, CouchDbJobStoreException {
        couchdbStore = new CouchDbStore();
        couchdbStore.setProperties("/couchdb.properties");
        couchSchedulerFactoryBean = new MotechSchedulerFactoryBean(applicationContext, couchdbSettingsFacade);
        jdbcSchedulerFactoryBean = new MotechSchedulerFactoryBean(applicationContext, jdbcSettingsFacade);
    }

    @After
    public void tearDown() throws SchedulerException {
    }

    @Test
    public void schedulingJobsInJdbcStore() throws Exception {
        scheduleJobs(jdbcSchedulerFactoryBean, jdbcSettingsFacade);
    }

    @Test
    public void schedulingJobsInCouchStore() throws Exception {
        scheduleJobs(couchSchedulerFactoryBean, couchdbSettingsFacade);
    }

    private void scheduleJobs(MotechSchedulerFactoryBean schedulerFactoryBean, SettingsFacade settings) throws InterruptedException, SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getQuartzScheduler();
        scheduler.clear();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("foo", "bar");
            params.put("fii", "bor");
            params.put("fuu", "baz");
            String eventSubject = "test_event";
            MotechEvent motechEvent = new MotechEvent(eventSubject, params);

            int maxJobs = 1;
            int repeatCount = 1;
            TestListener listener = new TestListener("test_listener", maxJobs, repeatCount);
            eventListenerRegistryService.registerListener(listener, eventSubject);

            DateTime startTime = now().plusSeconds(0);
            Long repeatInterval = 1L;
            System.out.println("startTime: " + startTime.toDate());

            MotechSchedulerServiceImpl schedulerService = new MotechSchedulerServiceImpl(schedulerFactoryBean, settings);
            NanoStopWatch timeToSchedule = new NanoStopWatch().start();
            for (int i = 0; i < maxJobs; i++) {
                params.put(MotechSchedulerService.JOB_ID_KEY, "test_job_" + String.valueOf(i));
                RepeatingSchedulableJob repeatingJob = new RepeatingSchedulableJob(motechEvent, startTime.toDate(), null, repeatCount, repeatInterval, false);
                repeatingJob.setUseOriginalFireTimeAfterMisfire(false);
                schedulerService.scheduleRepeatingJob(repeatingJob);
            }
            System.out.println(format("Time to schedule : %dms", timeToSchedule.duration()));

            scheduler.start();
            synchronized (listener) {
                while (scheduler.getTriggerKeys(GroupMatcher.triggerGroupContains("default")).size() > 0) {
                    listener.wait(5000);
                }
            }
            System.out.println(listener.getReport());

            assertEquals(maxJobs * (repeatCount + 1), listener.getCount());
        } finally {
            eventListenerRegistryService.clearListenersForBean("test_listener");
            schedulerFactoryBean.getQuartzScheduler().standby();
        }
    }
}

class TestListener implements EventListener {

    String identifier;
    final int maxJobs;
    final int repeat;
    volatile int count;
    String report = "";
    NanoStopWatch timer = new NanoStopWatch();

    TestListener(String identifier, int maxJobs, int repeat) {
        this.identifier = identifier;
        this.maxJobs = maxJobs;
        this.repeat = repeat;
    }

    @Override
    public void handle(MotechEvent event) {
        if (count == 0) {
            timer.start();
        }
        if (count % maxJobs == 0) {
            timer.start();
        }
        synchronized (TestListener.class) {
            count++;
        }
        System.out.println(count + "; triggered " + event.getParameters().get(MotechSchedulerService.JOB_ID_KEY) + " at " + new Date());
        if (count % maxJobs == 0) {
            report += "Time to receive next " + maxJobs + " events: " + (timer.duration() / 1000000L) + "ms\n";
        }
        if (count == maxJobs * (repeat + 1)) {
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public int getCount() {
        return count;
    }

    public String getReport() {
        return report;
    }
}
