package org.motechproject.scheduler.it;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Period;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.scheduler.contract.CronJobId;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.contract.DayOfWeekSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.scheduler.exception.MotechSchedulerException;
import org.motechproject.scheduler.factory.MotechSchedulerFactoryBean;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.commons.date.util.DateUtil.newDate;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.scheduler.IdGenerator.id;
import static org.motechproject.scheduler.TimeFaker.fakeNow;
import static org.motechproject.scheduler.TimeFaker.stopFakingTime;
import static org.quartz.TriggerKey.triggerKey;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MotechSchedulerServiceImplBundleIT extends BasePaxIT {

    @Inject
    private BundleContext context;

    @Inject
    @Filter(timeout = 360000)
    private EventListenerRegistryService eventRegistry;

    @Inject
    @Filter(timeout = 360000)
    private MotechSchedulerService schedulerService;

    MotechSchedulerFactoryBean motechSchedulerFactoryBean;

    Scheduler scheduler;

    @Before
    public void setup() {
        motechSchedulerFactoryBean = (MotechSchedulerFactoryBean) getBeanFromBundleContext(context,
                "org.motechproject.motech-scheduler", "motechSchedulerFactoryBean");
        scheduler = motechSchedulerFactoryBean.getQuartzScheduler();
    }

    @After
    public void tearDown() throws SchedulerException {
        schedulerService.unscheduleAllJobs("test_event");
        schedulerService.unscheduleAllJobs("test_event_2");
        schedulerService.unscheduleAllJobs("test_event_3");
    }

    @Test
    public void shouldScheduleCronJob() throws SchedulerException {
        try {
            fakeNow(new DateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            final String job_id = id("job_id");
            params.put(MotechSchedulerService.JOB_ID_KEY, job_id);
            schedulerService.scheduleJob(
                    new CronSchedulableJob(
                            new MotechEvent("test_event", params),
                            "0 0 10 * * ?"
                    ));

            List<DateTime> first3FireTimes = getFireTimes("test_event-" + job_id).subList(0, 3);
            assertEquals(asList(
                    newDateTime(2020, 7, 15, 10, 0, 0),
                    newDateTime(2020, 7, 16, 10, 0, 0),
                    newDateTime(2020, 7, 17, 10, 0, 0)),
                    first3FireTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldIgnoreFiresInPastWhenSchedulingCronJob() throws InterruptedException, SchedulerException {
        String subject = "cron_ignore_misfire";
        try {
            TestEventListener listener = new TestEventListener();
            eventRegistry.registerListener(listener, subject);

            DateTime now;
            for (now = now(); now.getSecondOfMinute() > 55 || now.getSecondOfMinute() < 5; now = now()) {   // we don't want triggers now, only misfires
                Thread.sleep(1000);
            }
            DateTime jobStartTime = now.minusMinutes(3);
            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            schedulerService.scheduleJob(new CronSchedulableJob(new MotechEvent(subject, params), "0 0/1 * 1/1 * ? *", jobStartTime.toDate(), null, true));

            synchronized (listener.getReceivedEvents()) {
                listener.getReceivedEvents().wait(2000);
            }
            assertTrue(listener.getReceivedEvents().size() == 0);
        } finally {
            eventRegistry.clearListenersForBean("test");
            schedulerService.unscheduleAllJobs(subject);
        }
    }

    @Test
    // org.quartz.jobStore.misfireThreshold=1000 (in quartz.properties) makes the test reliable.
    // See http://quartz-scheduler.org/documentation/quartz-2.x/configuration/ConfigJobStoreTX
    public void shouldNotIgnoreFiresInPastWhenSchedulingCronJob() throws InterruptedException, SchedulerException {
        final String eventSubject = id("eve");
        try {
            TestEventListener listener = new TestEventListener();
            eventRegistry.registerListener(listener, eventSubject);

            DateTime now = findSuitableTimeToScheduleWithSafeBufferFromTriggerTime();
            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            DateTime jobStartTimeInPast = now.minusMinutes(3);
            schedulerService.scheduleJob(new CronSchedulableJob(new MotechEvent(eventSubject, params),
                    "0 0/1 * 1/1 * ? *", jobStartTimeInPast.toDate(), null, false));

            synchronized (listener.getReceivedEvents()) {
                listener.getReceivedEvents().wait(5000);
            }
            assertTrue("Listener didn't receive misfired events.", listener.getReceivedEvents().size() > 0);
        } finally {
            eventRegistry.clearListenersForBean(eventSubject);
            schedulerService.unscheduleAllJobs(eventSubject+ "-job_id");
        }
    }

    private DateTime findSuitableTimeToScheduleWithSafeBufferFromTriggerTime() throws InterruptedException {
        DateTime now;
        for (now = now(); now.getSecondOfMinute() >= 50 || now.getSecondOfMinute() <= 10; now = now()) {
            Thread.sleep(1000);
        }
        return now;
    }

    @Test
    public void shouldRescheduleCronJob() throws SchedulerException {
        try {
            fakeNow(new DateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            final String jobId = id("jobId");
            params.put(MotechSchedulerService.JOB_ID_KEY, jobId);
            schedulerService.scheduleJob(
                    new CronSchedulableJob(
                            new MotechEvent("test_event", params),
                            "0 0 10 * * ?"
                    ));

            schedulerService.scheduleJob(
                    new CronSchedulableJob(
                            new MotechEvent("test_event", params),
                            "0 0 14 * * ?"
                    ));

            List<DateTime> first3FireTimes = getFireTimes("test_event-" + jobId).subList(0, 3);
            assertEquals(asList(
                    newDateTime(2020, 7, 15, 14, 0, 0),
                    newDateTime(2020, 7, 16, 14, 0, 0),
                    newDateTime(2020, 7, 17, 14, 0, 0)),
                    first3FireTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldRescheduleCronJobWithNewSchedule() throws SchedulerException {
        try {
            fakeNow(new DateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            schedulerService.scheduleJob(
                    new CronSchedulableJob(
                            new MotechEvent("test_event", params),
                            "0 0 10 * * ?"
                    ));

            schedulerService.rescheduleJob("test_event", "job_id", "0 0 14 * * ?");

            List<DateTime> first3FireTimes = getFireTimes("test_event-job_id").subList(0, 3);
            assertEquals(asList(
                    newDateTime(2020, 7, 15, 14, 0, 0),
                    newDateTime(2020, 7, 16, 14, 0, 0),
                    newDateTime(2020, 7, 17, 14, 0, 0)),
                    first3FireTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldGetPreviousFireTime() throws InterruptedException {
        try {
            fakeNow(new DateTime());

            Map<String, Object> params = new HashMap<>();
            MotechEvent event = new MotechEvent("test_event", params);
            final String jobId = id("jobId");
            params.put(MotechSchedulerService.JOB_ID_KEY, jobId);
            DateTime now = new DateTime();
            StringBuilder cron = new StringBuilder();
            cron.append(now.getSecondOfMinute()).append(" ").append(now.getMinuteOfHour()).append(" ");
            cron.append(now.getHourOfDay()).append(" * * ?");
            schedulerService.scheduleJob(
                    new CronSchedulableJob(
                            event,
                            cron.toString()
                    ));
            Thread.sleep(1000);
            DateTime dateTime = schedulerService.getPreviousFireDate(new CronJobId(event));
            assertEquals(dateTime.getHourOfDay(), now.getHourOfDay());
            assertEquals(dateTime.getMinuteOfHour(), now.getMinuteOfHour());
            assertEquals(dateTime.getSecondOfMinute(), now.getSecondOfMinute());
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldGetNextFireTime() {
        try {
            DateTime fireDate = new DateTime(2020, 7, 15, 10, 0, 0);
            fakeNow(fireDate);

            Map<String, Object> params = new HashMap<>();
            MotechEvent event = new MotechEvent("test_event", params);
            final String jobId = id("jobId");
            params.put(MotechSchedulerService.JOB_ID_KEY, jobId);
            schedulerService.scheduleJob(
                    new CronSchedulableJob(
                            event,
                            "0 0 10 * * ?"
                    ));

            DateTime dateTime = schedulerService.getNextFireDate(new CronJobId(event));
            assertEquals(fireDate, dateTime);
        } finally {
            stopFakingTime();
        }
    }

    @Test(expected = MotechSchedulerException.class)
    public void shouldThrowExceptionForInvalidCronExpression() throws SchedulerException {
        Map<String, Object> params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
        schedulerService.scheduleJob(new CronSchedulableJob(new MotechEvent("test_event", params), "invalidCronExpression"));
    }

    @Test(expected = MotechSchedulerException.class)
    public void shouldThrowExceptionForInvalidCronExpressionWhenreschedulingJob() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
        schedulerService.scheduleJob(new CronSchedulableJob(new MotechEvent("test_event", params), "0 0 10 * * ?"));
        schedulerService.rescheduleJob("test_event", "job_id", "invalidCronExpression");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullCronJob() throws Exception {
        schedulerService.scheduleJob(null);
    }

    @Test
    public void shouldScheduleRunOnceJob() throws SchedulerException {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            schedulerService.scheduleRunOnceJob(
                    new RunOnceSchedulableJob(
                            new MotechEvent("test_event", params),
                            newDateTime(2020, 7, 15, 12, 0, 0).toDate()
                    ));

            List<DateTime> fireTimes = getFireTimes("test_event-job_id-runonce");
            assertEquals(asList(
                    newDateTime(2020, 7, 15, 12, 0, 0)),
                    fireTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotScheduleRunOnceJobInThePast() throws SchedulerException {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            schedulerService.scheduleRunOnceJob(
                new RunOnceSchedulableJob(
                    new MotechEvent("test_event", params),
                    newDateTime(2020, 6, 15, 12, 0, 0).toDate()
                ));
        } finally {
            stopFakingTime();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullRunOneceJob() throws Exception {
        schedulerService.scheduleRunOnceJob(null);
    }

    @Test
    public void shouldScheduleRepeatJobBoundByCount() throws SchedulerException {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            final String jobId = id("jobId");
            params.put(MotechSchedulerService.JOB_ID_KEY, jobId);
            schedulerService.scheduleRepeatingJob(
                    new RepeatingSchedulableJob(
                            new MotechEvent("test_event", params),
                            newDateTime(2020, 7, 15, 12, 0, 0).toDate(),
                            null,
                            2,
                            (long) DateTimeConstants.MILLIS_PER_DAY,
                            false)
            );

            List<DateTime> fireTimes = getFireTimes("test_event-" + jobId + "-repeat");
            assertEquals(asList(
                    newDateTime(2020, 7, 15, 12, 0, 0),
                    newDateTime(2020, 7, 16, 12, 0, 0),
                    newDateTime(2020, 7, 17, 12, 0, 0)),
                    fireTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldScheduleRepeatJobBoundByEndDate() throws SchedulerException {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));
            final String jobId = id("jobId");

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, jobId);
            schedulerService.scheduleRepeatingJob(
                    new RepeatingSchedulableJob(
                            new MotechEvent("test_event", params),
                            newDateTime(2020, 7, 15, 12, 0, 0).toDate(),
                            newDateTime(2020, 7, 18, 12, 0, 0).toDate(),
                            (long) DateTimeConstants.MILLIS_PER_DAY,
                            false)
            );

            List<DateTime> fireTimes = getFireTimes("test_event-" + jobId + "-repeat");
            assertEquals(asList(
                    newDateTime(2020, 7, 15, 12, 0, 0),
                    newDateTime(2020, 7, 16, 12, 0, 0),
                    newDateTime(2020, 7, 17, 12, 0, 0)),
                    fireTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldScheduleRepeatingPeriodSchedulableJob() throws SchedulerException {
        try {
            fakeNow(new DateTime(2020, 7 ,15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            schedulerService.scheduleRepeatingPeriodJob(
                    new RepeatingPeriodSchedulableJob(
                            new MotechEvent("test_event_3", params),
                            newDateTime(2020, 7, 15, 12, 0, 0).toDate(),
                            newDateTime(2020, 7, 16, 12, 0, 0).toDate(),
                            new Period(4, 0, 0, 0),
                            true
                    )
            );

            List<DateTime> fireTimes = getFireTimes("test_event_3-job_id-period");
            assertEquals(asList(
                    new DateTime(2020, 7, 15, 12, 0, 0),
                    new DateTime(2020, 7, 15, 16, 0, 0),
                    new DateTime(2020, 7, 15, 20, 0, 0),
                    new DateTime(2020, 7, 16, 0, 0, 0),
                    new DateTime(2020, 7, 16, 4, 0, 0),
                    new DateTime(2020, 7, 16, 8, 0, 0),
                    new DateTime(2020, 7, 16, 12, 0, 0)
            ), fireTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldScheduleInterveningRepeatJob() throws SchedulerException {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            schedulerService.scheduleRepeatingJob(
                    new RepeatingSchedulableJob(
                            new MotechEvent("test_event", params),
                            newDateTime(2020, 7, 14, 12, 0, 0).toDate(),
                            newDateTime(2020, 7, 18, 12, 0, 0).toDate(),
                            (long) DateTimeConstants.MILLIS_PER_DAY,
                            true)
            );

            List<DateTime> fireTimes = getFireTimes("test_event-job_id-repeat");
            assertEquals(asList(
                    newDateTime(2020, 7, 15, 12, 0, 0),
                    newDateTime(2020, 7, 16, 12, 0, 0),
                    newDateTime(2020, 7, 17, 12, 0, 0)),
                    fireTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldScheduleInterveningRepeatJobWithoutEndDate() throws SchedulerException {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            RepeatingSchedulableJob repeatJob = new RepeatingSchedulableJob(
                new MotechEvent("test_event", params),
                newDateTime(2020, 7, 13, 12, 0, 0).toDate(),
                null,
                3,
                (long) DateTimeConstants.MILLIS_PER_DAY,
                true);
            repeatJob.setUseOriginalFireTimeAfterMisfire(false);
            schedulerService.scheduleRepeatingJob(repeatJob);

            List<DateTime> fireTimes = getFireTimes("test_event-job_id-repeat");
            assertEquals(asList(
                    newDateTime(2020, 7, 15, 12, 0, 0),
                    newDateTime(2020, 7, 16, 12, 0, 0)),
                    fireTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullRepeatJob() throws Exception {
        schedulerService.scheduleRepeatingJob(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullMotechEvent() throws SchedulerException {
        Map<String, Object> params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
        schedulerService.scheduleRepeatingJob(
                new RepeatingSchedulableJob(
                        null,
                        newDateTime(2020, 7, 15, 12, 0, 0).toDate(),
                        newDateTime(2020, 7, 18, 12, 0, 0).toDate(),
                        (long) DateTimeConstants.MILLIS_PER_DAY,
                        false)
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullStartTime() throws SchedulerException {
        Map<String, Object> params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
        schedulerService.scheduleRepeatingJob(
                new RepeatingSchedulableJob(
                        new MotechEvent("test_event", params),
                        null,
                        newDateTime(2020, 7, 18, 12, 0, 0).toDate(),
                        (long) DateTimeConstants.MILLIS_PER_DAY,
                        false)
        );
    }

    @Test
    public void shouldScheduleDayOfWeekJob() throws SchedulerException {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            schedulerService.scheduleDayOfWeekJob(
                    new DayOfWeekSchedulableJob(
                            new MotechEvent("test_event", params),
                            newDate(2020, 7, 10),   // friday
                            newDate(2020, 7, 22),
                            asList(DayOfWeek.Monday, DayOfWeek.Thursday),
                            new Time(10, 10),
                            false)
            );

            List<DateTime> fireTimes = getFireTimes("test_event-job_id");
            assertEquals(asList(
                    newDateTime(2020, 7, 13, 10, 10, 0),
                    newDateTime(2020, 7, 16, 10, 10, 0),
                    newDateTime(2020, 7, 20, 10, 10, 0)),
                    fireTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldScheduleInterveningDayOfWeekJob() throws SchedulerException {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            schedulerService.scheduleDayOfWeekJob(
                    new DayOfWeekSchedulableJob(
                            new MotechEvent("test_event", params),
                            newDate(2020, 7, 10),   // friday
                            newDate(2020, 7, 22),
                            asList(DayOfWeek.Monday, DayOfWeek.Thursday),
                            new Time(10, 10),
                            true)
            );

            List<DateTime> fireTimes = getFireTimes("test_event-job_id");
            assertEquals(asList(
                    newDateTime(2020, 7, 16, 10, 10, 0),
                    newDateTime(2020, 7, 20, 10, 10, 0)),
                    fireTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldUnscheduleJob() throws SchedulerException {
        Map<String, Object> params = new HashMap<>();
        final String jobId = id("jobId");
        params.put(MotechSchedulerService.JOB_ID_KEY, jobId);
        schedulerService.scheduleJob(new CronSchedulableJob(new MotechEvent("test_event", params), "0 0 12 * * ?"));

        schedulerService.unscheduleJob("test_event", jobId);

        assertNull(scheduler.getTrigger(triggerKey("test_event-" + jobId, "default")));
    }

    @Test
    public void shouldUnscheduleAllJobsWithAGivenJobIdPrefix() throws SchedulerException {
        Map<String, Object> params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");

        schedulerService.scheduleJob(new CronSchedulableJob(new MotechEvent("test_event1", params), "0 0 12 * * ?"));
        schedulerService.scheduleJob(new CronSchedulableJob(new MotechEvent("test_event2", params), "0 0 13 * * ?"));
        schedulerService.scheduleJob(new CronSchedulableJob(new MotechEvent("test_event3", params), "0 0 14 * * ?"));

        schedulerService.unscheduleAllJobs("test_event");

        assertNull(scheduler.getTrigger(triggerKey("test_event1-job_id", "default")));
        assertNull(scheduler.getTrigger(triggerKey("test_event2-job_id", "default")));
        assertNull(scheduler.getTrigger(triggerKey("test_event3-job_id", "default")));
    }

    private List<DateTime> getFireTimes(String triggerKey) throws SchedulerException {
        Trigger trigger = scheduler.getTrigger(triggerKey(triggerKey, "default"));
        List<DateTime> fireTimes = new ArrayList<>();
        Date nextFireTime = trigger.getNextFireTime();
        while (nextFireTime != null) {
            fireTimes.add(newDateTime(nextFireTime));
            nextFireTime = trigger.getFireTimeAfter(nextFireTime);
        }
        return fireTimes;
    }
}

class TestEventListener implements EventListener {

    private List<MotechEvent> receivedEvents = new ArrayList<>();

    @Override
    public void handle(MotechEvent event) {
        synchronized (receivedEvents) {
            receivedEvents.add(event);
            receivedEvents.notify();
        }
    }

    @Override
    public String getIdentifier() {
        return "test";
    }

    public List<MotechEvent> getReceivedEvents() {
        return receivedEvents;
    }

}
