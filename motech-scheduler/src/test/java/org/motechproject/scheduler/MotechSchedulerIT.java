package org.motechproject.scheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.*;
import org.junit.runner.RunWith;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.scheduler.domain.*;
import org.motechproject.scheduler.exception.MotechSchedulerException;
import org.motechproject.scheduler.impl.MotechSchedulerServiceImpl;
import org.motechproject.server.event.EventListenerRegistry;
import org.motechproject.server.event.annotations.MotechListenerEventProxy;
import org.motechproject.util.DateUtil;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.OperableTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.fakeToday;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerKey.triggerKey;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testSchedulerApplicationContext.xml"})
public class MotechSchedulerIT {

    public static final String SUBJECT = "testEvent";
    private static final int REPEAT_INTERVAL_IN_SECONDS = 5;
    @Autowired
    private MotechSchedulerService motechSchedulerService;

    @Autowired
    private EventListenerRegistry eventListenerRegistry;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private TestJobHandler testJobHandler;

    private String uuidStr = UUID.randomUUID().toString();

    private long scheduledHour;
    private long scheduledMinute;
    private long scheduledSecond;

    private boolean executed;

    @Before
    public void setUp() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(1, Calendar.MINUTE);
        DateTime now = DateUtil.now();

        scheduledHour = now.getHourOfDay();
        scheduledMinute = now.getMinuteOfHour() + 1;
        scheduledSecond = now.getSecondOfMinute();
        if (scheduledMinute == 59) {
            scheduledHour = (scheduledHour + 1) % 24;
            scheduledMinute = 0;
        }
    }

    @Test
    @Ignore
    public void scheduleCronJobTest() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", UUID.randomUUID().toString());
        String testSubject = "MotechSchedulerITScheduleTest";
        MotechEvent motechEvent = new MotechEvent(testSubject, params);

        Method handlerMethod = ReflectionUtils.findMethod(MotechSchedulerIT.class, "handleEvent", MotechEvent.class);
        eventListenerRegistry.registerListener(new MotechListenerEventProxy("handleEvent", this, handlerMethod), testSubject);

        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, String.format("%d %d %d * * ?", scheduledSecond, scheduledMinute, scheduledHour));
        motechSchedulerService.scheduleJob(cronSchedulableJob);

        //Thread.sleep(90000);   // seems to pass without sleep
        if (!executed) {
            Assert.fail("scheduler job not handled ..........");
        }
    }

    public void handleEvent(MotechEvent motechEvent) {
        System.out.println(motechEvent.getSubject());
        executed = true;
    }

    @Test
    public void scheduleTest() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0 0 12 * * ?");

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size();

        motechSchedulerService.scheduleJob(cronSchedulableJob);

        assertEquals(scheduledJobsNum + 1, schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size());
    }

    @Test
    public void scheduleExistingJobTest() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0 0 12 * * ?");

        Map<String, Object> newParameters = new HashMap<String, Object>();
        newParameters.put("param1", "value1");
        newParameters.put("JobID", uuidStr);

        String newCronExpression = "0 0 0 * * ?";
        MotechEvent newMotechEvent = new MotechEvent("testEvent", newParameters);
        CronSchedulableJob newCronSchedulableJob = new CronSchedulableJob(newMotechEvent, newCronExpression);

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size();

        motechSchedulerService.scheduleJob(cronSchedulableJob);
        assertEquals(scheduledJobsNum + 1, schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size());

        motechSchedulerService.scheduleJob(newCronSchedulableJob);
        assertEquals(scheduledJobsNum + 1, schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size());

        CronTrigger trigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(triggerKey("testEvent-" + uuidStr, MotechSchedulerServiceImpl.JOB_GROUP_NAME));
        JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobKey("testEvent-" + uuidStr, MotechSchedulerServiceImpl.JOB_GROUP_NAME));
        JobDataMap jobDataMap = jobDetail.getJobDataMap();

        assertEquals(newCronExpression, trigger.getCronExpression());
        assertEquals(3, jobDataMap.size());
    }

    @Test(expected = MotechSchedulerException.class)
    public void scheduleInvalidCronExprTest() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, " ?");

        motechSchedulerService.scheduleJob(cronSchedulableJob);
    }

    @Test(expected = IllegalArgumentException.class)
    public void scheduleNullJobTest() throws Exception {
        motechSchedulerService.scheduleJob(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void scheduleNullRunOnceJobTest() throws Exception {
        motechSchedulerService.scheduleRunOnceJob(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void scheduleNullRepeatingJobTest() throws Exception {
        motechSchedulerService.scheduleRepeatingJob(null);
    }

    @Test
    public void updateScheduledJobHappyPathTest() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0 0 12 * * ?");

        motechSchedulerService.scheduleJob(cronSchedulableJob);

        String patientIdKeyName = "patientId";
        String patientId = "1";
        params = new HashMap<String, Object>();
        params.put(patientIdKeyName, patientId);
        params.put("JobID", uuidStr);

        motechEvent = new MotechEvent("testEvent", params);

        motechSchedulerService.updateScheduledJob(motechEvent);

        JobDataMap jobDataMap = schedulerFactoryBean.getScheduler().getJobDetail(jobKey("testEvent-" + uuidStr, MotechSchedulerServiceImpl.JOB_GROUP_NAME)).getJobDataMap();

        assertEquals(patientId, jobDataMap.getString(patientIdKeyName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateScheduledJobNullTest() throws Exception {
        motechSchedulerService.updateScheduledJob(null);
    }

    @Test
    public void rescheduleJobHappyPathTest() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0 0 12 * * ?");

        motechSchedulerService.scheduleJob(cronSchedulableJob);

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size();

        String newCronExpression = "0 0 10 * * ?";

        motechSchedulerService.rescheduleJob("testEvent", uuidStr, newCronExpression);
        assertEquals(scheduledJobsNum, schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size());

        CronTrigger trigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(triggerKey("testEvent-" + uuidStr, MotechSchedulerServiceImpl.JOB_GROUP_NAME));
        String triggerCronExpression = trigger.getCronExpression();

        assertEquals(newCronExpression, triggerCronExpression);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rescheduleJobNullJobIdTest() {
        motechSchedulerService.rescheduleJob(null, null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rescheduleJobNullCronExpressionTest() {
        motechSchedulerService.rescheduleJob("", "", null);
    }

    @Test(expected = MotechSchedulerException.class)
    public void rescheduleJobInvalidCronExpressionTest() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0 0 12 * * ?");

        motechSchedulerService.scheduleJob(cronSchedulableJob);

        motechSchedulerService.rescheduleJob("testEvent", uuidStr, "?");
    }

    @Test(expected = MotechSchedulerException.class)
    public void rescheduleNotExistingJobTest() {
        motechSchedulerService.rescheduleJob("", "0", "?");
    }

    @Test
    public void scheduleRunOnceJobTest() throws Exception {

        String uuidStr = UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("TestEvent", params);
        RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(motechEvent, new Date((new Date()).getTime() + 5000));

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size();

        motechSchedulerService.scheduleRunOnceJob(schedulableJob);

        assertEquals(scheduledJobsNum + 1, schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void scheduleRunOncePastJobTest() throws Exception {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        MotechEvent motechEvent = new MotechEvent(null, null);

        RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(motechEvent, calendar.getTime());

        schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME));
        motechSchedulerService.scheduleRunOnceJob(schedulableJob);
    }

    @Test
    public void testScheduleRepeatingJob() throws Exception {
        String uuidStr = UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("TestEvent", params);

        Calendar cal = Calendar.getInstance();
        Date start = cal.getTime();
        cal.add(Calendar.DATE, 1);
        Date end = cal.getTime();

        RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(motechEvent, start, end, 5, 5000L, false);

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size();

        motechSchedulerService.scheduleRepeatingJob(schedulableJob);

        GroupMatcher<TriggerKey> bar = GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME);
        Set<TriggerKey> foo = schedulerFactoryBean.getScheduler().getTriggerKeys(bar);
        final List<? extends Trigger> triggersOfJob = schedulerFactoryBean.getScheduler().getTriggersOfJob(JobKey.jobKey(new RepeatingJobId(motechEvent).value(), "default"));
        assertEquals(new Date(start.getTime() + 5 * 5000), triggersOfJob.get(0).getFinalFireTime());
        assertEquals(scheduledJobsNum + 1, foo.size());
    }

    @Test
    public void testScheduleInterveningRepeatingJob() throws Exception {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 10, 30));
            String uuidStr = UUID.randomUUID().toString();

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("JobID", uuidStr);
            MotechEvent motechEvent = new MotechEvent("TestEvent", params);

            Date start = newDateTime(2020, 7, 15, 10, 10, 22).toDate();
            Date end = newDateTime(2020, 7, 15, 10, 11, 0).toDate();
            RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(motechEvent, start, end, 3, 5000L, true);

            motechSchedulerService.scheduleRepeatingJob(schedulableJob);

            Trigger trigger = schedulerFactoryBean.getScheduler().getTrigger(triggerKey(new RepeatingJobId(motechEvent).value(), "default"));
            assertEquals(newDateTime(2020, 7, 15, 10, 10, 32).toDate(), trigger.getNextFireTime());
            assertEquals(newDateTime(2020, 7, 15, 10, 10, 37).toDate(), trigger.getFireTimeAfter(newDateTime(2020, 7, 15, 10, 10, 32).toDate()));
            assertEquals(null, trigger.getFireTimeAfter(newDateTime(2020, 7, 15, 10, 10, 37).toDate()));
        } finally {
            stopFakingTime();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleRepeatingJobTest_NoStartDate() throws Exception {
        String uuidStr = UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("TestEvent", params);
        RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(motechEvent, null, new Date(), 5, 5000L, false);
        schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME));
        motechSchedulerService.scheduleRepeatingJob(schedulableJob);
    }

    @Test
    public void testScheduleRepeatingJobTest_NoEndDate() throws Exception {
        String uuidStr = UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("TestEvent", params);
        RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(motechEvent, new Date(), null, 5, 5000L, false);

        schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME));

        motechSchedulerService.scheduleRepeatingJob(schedulableJob);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleRepeatingJobTest_NullJob() throws Exception {
        RepeatingSchedulableJob schedulableJob = null;
        schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME));
        motechSchedulerService.scheduleRepeatingJob(schedulableJob);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleRepeatingJobTest_NullEvent() throws Exception {
        MotechEvent motechEvent = null;
        RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(motechEvent, new Date(), new Date(), 5, 5000L, false);

        schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME));

        motechSchedulerService.scheduleRepeatingJob(schedulableJob);
    }

    @Test
    public void testScheduleDayOfWeekJob() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", UUID.randomUUID().toString());
        MotechEvent motechEvent = new MotechEvent("TestEvent", params);

        LocalDate start = newDate(2020, 7, 10); // friday
        LocalDate end = start.plusDays(10);
        DayOfWeekSchedulableJob schedulableJob = new DayOfWeekSchedulableJob(motechEvent, start, end, asList(DayOfWeek.Monday, DayOfWeek.Thursday), new Time(10, 10), executed);

        motechSchedulerService.scheduleDayOfWeekJob(schedulableJob);

        Trigger trigger = schedulerFactoryBean.getScheduler().getTrigger(triggerKey(new CronJobId(motechEvent).value(), "default"));
        assertEquals(newDateTime(2020, 7, 13, 10, 10, 0).toDate(), trigger.getNextFireTime());
        assertEquals(newDateTime(2020, 7, 16, 10, 10, 0).toDate(), trigger.getFireTimeAfter(newDateTime(2020, 7, 13, 10, 10, 0).toDate()));
        assertEquals(null, trigger.getFireTimeAfter(newDateTime(2020, 7, 16, 10, 10, 0).toDate()));
    }

    @Test
    public void testScheduleInterveningDayOfWeekJob() throws Exception {
        try {
            fakeToday(new LocalDate(2020, 7, 15));
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("JobID", UUID.randomUUID().toString());
            MotechEvent motechEvent = new MotechEvent("TestEvent", params);

            LocalDate start = newDate(2020, 7, 10); // friday
            LocalDate end = start.plusDays(10);
            DayOfWeekSchedulableJob schedulableJob = new DayOfWeekSchedulableJob(motechEvent, start, end, asList(DayOfWeek.Monday, DayOfWeek.Thursday), new Time(10, 10), true);

            motechSchedulerService.scheduleDayOfWeekJob(schedulableJob);

            Trigger trigger = schedulerFactoryBean.getScheduler().getTrigger(triggerKey(new CronJobId(motechEvent).value(), "default"));
            assertEquals(newDateTime(2020, 7, 16, 10, 10, 0).toDate(), trigger.getNextFireTime());
            assertEquals(null, trigger.getFireTimeAfter(newDateTime(2020, 7, 16, 10, 10, 0).toDate()));
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void unscheduleJobTest() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent(SUBJECT, params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0 0 12 * * ?");

        motechSchedulerService.scheduleJob(cronSchedulableJob);
        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size();

        motechSchedulerService.unscheduleJob(SUBJECT, uuidStr);

        assertEquals(scheduledJobsNum - 1, schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size());
    }

    @Test
    public void unscheduleJobsGivenAJobIdPrefix() throws Exception {

        motechSchedulerService.scheduleJob(getJob("testJobId.1.1"));
        motechSchedulerService.scheduleJob(getJob("testJobId.1.2"));
        motechSchedulerService.scheduleJob(getJob("testJobId.1.3"));

        int numOfScheduledJobs = schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size();

        motechSchedulerService.unscheduleAllJobs("testJobId");

        assertEquals(numOfScheduledJobs - 3, schedulerFactoryBean.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(MotechSchedulerServiceImpl.JOB_GROUP_NAME)).size());
    }

    @Test
    public void shouldGetJobTimingsForJobId() {
        String jobId = "testJob";
        CronSchedulableJob job = getJob(jobId);
        motechSchedulerService.scheduleJob(job);

        List<Date> dateList = motechSchedulerService.getScheduledJobTimings(job.getMotechEvent().getSubject(),
                jobId, DateTime.now().toDate(), DateTime.now().plusDays(10).toDate());

        assertEquals(10, dateList.size());
    }

    @Test
    public void shouldGetJobTimingsForJobIdPrefix() {
        String jobId = "testJob-repeat";
        CronSchedulableJob job = getJob(jobId);
        motechSchedulerService.scheduleJob(job);

        List<Date> dateList = motechSchedulerService.getScheduledJobTimingsWithPrefix(job.getMotechEvent().getSubject(),
                jobId, DateTime.now().toDate(), DateTime.now().plusDays(10).toDate());

        assertEquals(10, dateList.size());
    }

    @Test
    public void shouldScheduleRepeatingJobAndVerifyPastMisfiresTriggeredOnce() throws Exception {
        schedulerFactoryBean.getScheduler().clear();
        final DateTime now = DateUtil.now().withMillisOfSecond(0);
        final DateTime startDate = now.minusSeconds(12);
        final Date endDate = startDate.plusDays(1).toDate();
        final MotechEvent event = new MotechEvent("TestSubject");
        final RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, startDate.toDate(), endDate, 3,
                REPEAT_INTERVAL_IN_SECONDS * 1000L, false);
        job.setUseOriginalFireTimeAfterMisfire(true);
        motechSchedulerService.safeScheduleRepeatingJob(job);

        Trigger trigger = schedulerFactoryBean.getScheduler().getTrigger(triggerKey(new RepeatingJobId(event).value(), "default"));
        final org.quartz.Calendar calendar = schedulerFactoryBean.getScheduler().getCalendar(trigger.getCalendarName());
        final List<Date> fireTimes = TriggerUtils.computeFireTimes((OperableTrigger) trigger, calendar, 10);
        Thread.sleep(6000);   //let the scheduler call test job handler.
        final List<DateTime> triggeredTime = testJobHandler.getTriggeredTime();
        assertDateEqualsIgnoreMillis(now.toDate(), triggeredTime.get(0));
        assertDateEqualsIgnoreMillis(startDate.plusSeconds(3 * REPEAT_INTERVAL_IN_SECONDS).toDate(), triggeredTime.get(1));
    }

    private void assertDateEqualsIgnoreMillis(Date date1, DateTime date2) {
        assertTrue("" + date1 + "|" + date2, Math.abs(date1.getTime() - date2.getMillis()) <1000L);
    }


    private CronSchedulableJob getJob(String jobId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", jobId);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        return new CronSchedulableJob(motechEvent, "0 0 12 * * ?");
    }
}
