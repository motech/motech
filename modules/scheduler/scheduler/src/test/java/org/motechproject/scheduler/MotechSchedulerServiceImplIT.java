package org.motechproject.scheduler;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.DayOfWeekSchedulableJob;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.scheduler.exception.MotechSchedulerException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;
import static org.motechproject.commons.date.util.DateUtil.newDate;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.quartz.TriggerKey.triggerKey;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testSchedulerApplicationContext.xml"})
public class MotechSchedulerServiceImplIT {

    @Autowired
    MotechSchedulerService schedulerService;

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

    Scheduler scheduler;

    @Before
    public void setup() {
        scheduler = schedulerFactoryBean.getScheduler();
    }

    @After
    public void teardown() {
        schedulerService.unscheduleAllJobs("test_event");
    }

    @Test
    public void shouldScheduleCronJob() throws SchedulerException {
        try {
            fakeNow(new DateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            schedulerService.scheduleJob(
                    new CronSchedulableJob(
                            new MotechEvent("test_event", params),
                            "0 0 10 * * ?"
                    ));

            List<DateTime> first3FireTimes = getFireTimes("test_event-job_id").subList(0, 3);
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
    public void shouldRescheduleCronJob() throws SchedulerException {
        try {
            fakeNow(new DateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
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

    @Test(expected = MotechSchedulerException.class)
    public void shouldThrowExceptionForInvalidCronExpression() throws SchedulerException {
        Map<String, Object> params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
        schedulerService.scheduleJob(new CronSchedulableJob(new MotechEvent("test_event", params), "goo"));
    }

    @Test(expected = MotechSchedulerException.class)
    public void shouldThrowExceptionForInvalidCronExpressionWhenreschedulingJob() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
        schedulerService.scheduleJob(new CronSchedulableJob(new MotechEvent("test_event", params), "0 0 10 * * ?"));
        schedulerService.rescheduleJob("test_event", "job_id", "goo");
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
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            schedulerService.scheduleRepeatingJob(
                    new RepeatingSchedulableJob(
                            new MotechEvent("test_event", params),
                            newDateTime(2020, 7, 15, 12, 0, 0).toDate(),
                            null,
                            2,
                            (long) DateTimeConstants.MILLIS_PER_DAY,
                            false)
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
    public void shouldScheduleRepeatJobBoundByEndDate() throws SchedulerException {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            schedulerService.scheduleRepeatingJob(
                    new RepeatingSchedulableJob(
                            new MotechEvent("test_event", params),
                            newDateTime(2020, 7, 15, 12, 0, 0).toDate(),
                            newDateTime(2020, 7, 18, 12, 0, 0).toDate(),
                            (long) DateTimeConstants.MILLIS_PER_DAY,
                            false)
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
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
        schedulerService.scheduleJob(new CronSchedulableJob(new MotechEvent("test_event", params), "0 0 12 * * ?"));

        schedulerService.unscheduleJob("test_event", "job_id");

        assertNull(scheduler.getTrigger(triggerKey("test_event-job_id", "default")));
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

    @Test
    public void shouldGetJobTimes() {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            schedulerService.scheduleJob(
                    new CronSchedulableJob(
                            new MotechEvent("test_event", params),
                            "0 0 12 * * ?"
                    ));

            List<Date> eventTimes = schedulerService.getScheduledJobTimings("test_event", "job_id", newDateTime(2020, 7, 15, 12, 0, 0).toDate(), newDateTime(2020, 7, 17, 12, 0, 0).toDate());
            assertEquals(asList(
                    newDateTime(2020, 7, 15, 12, 0, 0).toDate(),
                    newDateTime(2020, 7, 16, 12, 0, 0).toDate(),
                    newDateTime(2020, 7, 17, 12, 0, 0).toDate()),
                    eventTimes);
        } finally {
            stopFakingTime();
        }
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
