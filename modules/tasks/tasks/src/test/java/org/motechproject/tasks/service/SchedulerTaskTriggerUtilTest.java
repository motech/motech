package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.scheduler.contract.CronJobId;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.contract.DayOfWeekSchedulableJob;
import org.motechproject.scheduler.contract.JobId;
import org.motechproject.scheduler.contract.RepeatingJobId;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
import org.motechproject.scheduler.contract.RunOnceJobId;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.scheduler.exception.MotechSchedulerException;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.tasks.domain.SchedulerTaskTriggerInformation;
import org.motechproject.tasks.domain.Task;
import org.motechproject.testing.utils.TimeFaker;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SchedulerTaskTriggerUtilTest {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yy-MM-dd HH:mm Z");

    private static final String MODULE_NAME = "org.motechproject.motech-tasks";
    private static final String VERSION = "0.27.0.SNAPSHOT";
    private static final String CHANNEL = "scheduler";
    private static final String BASE_SUBJECT = "org.motechproject.tasks.scheduler.";

    @Mock
    private TaskService taskService;

    @Mock
    private MotechSchedulerService schedulerService;

    @Mock
    private Task task;

    @InjectMocks
    private SchedulerTaskTriggerUtil schedulerTaskTriggerUtil = new SchedulerTaskTriggerUtil();

    private String taskName;
    private SchedulerTaskTriggerInformation triggerInformation;

    @Before
    public void setUp() {
        initMocks(this);
        TimeFaker.fakeNow(new DateTime(2015, 9, 15, 8, 0, 0));
    }

    @After
    public void tearDown() {
        TimeFaker.stopFakingTime();
    }

    @Test
    public void shouldScheduleJobForRunOnceTrigger() {
        taskName = "Run Once Task Test";
        triggerInformation = new SchedulerTaskTriggerInformation("scheduler.runOnceJobTrigger", CHANNEL, MODULE_NAME,
                VERSION, BASE_SUBJECT + "runOnceJob.", BASE_SUBJECT + "runOnceJob." + taskName);
        triggerInformation.setType(SchedulerTaskTriggerInformation.SchedulerJobType.RUN_ONCE_JOB);
        triggerInformation.setStartDate("2015-09-25 08:00 +0200");

        when(taskService.findTasksByName(taskName)).thenReturn(Arrays.asList(task));
        when(task.getTrigger()).thenReturn(triggerInformation);

        schedulerTaskTriggerUtil.scheduleTriggerJob("org.motechproject.tasks.scheduler.runOnceJob." + taskName);

        ArgumentCaptor<RunOnceSchedulableJob> captor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(schedulerService).safeScheduleRunOnceJob(captor.capture());

        RunOnceSchedulableJob actual = captor.getValue();

        assertEquals(BASE_SUBJECT + "runOnceJob." + taskName, actual.getMotechEvent().getSubject());
        assertEquals(actual.getStartDate(), DATE_FORMAT.parseDateTime(triggerInformation.getStartDate()).toDate());
    }

    @Test(expected = MotechSchedulerException.class)
    public void shouldRejectPastDateForRunOnceJobScheduling() {
        taskName = "Run Once Task Test";
        triggerInformation = new SchedulerTaskTriggerInformation("scheduler.runOnceJobTrigger", CHANNEL, MODULE_NAME,
                VERSION, BASE_SUBJECT + "runOnceJob.", BASE_SUBJECT + "runOnceJob." + taskName);
        triggerInformation.setType(SchedulerTaskTriggerInformation.SchedulerJobType.RUN_ONCE_JOB);
        triggerInformation.setStartDate("2015-09-10 08:00 +0200");

        when(taskService.findTasksByName(taskName)).thenReturn(Arrays.asList(task));
        when(task.getTrigger()).thenReturn(triggerInformation);

        try {
            schedulerTaskTriggerUtil.scheduleTriggerJob("org.motechproject.tasks.scheduler.runOnceJob." + taskName);
        } finally {
            verify(schedulerService, never()).safeScheduleRunOnceJob(any(RunOnceSchedulableJob.class));
        }
    }

    @Test
    public void shouldScheduleJobForRepeatingTrigger() {
        taskName = "Repeating Task Test";
        triggerInformation = new SchedulerTaskTriggerInformation("scheduler.repeatingJobTrigger", CHANNEL, MODULE_NAME,
                VERSION, BASE_SUBJECT + "runOnceJob.", BASE_SUBJECT + "runOnceJob." + taskName);
        triggerInformation.setType(SchedulerTaskTriggerInformation.SchedulerJobType.REPEATING_JOB);
        triggerInformation.setStartDate("2015-09-25 08:00 +0200");
        triggerInformation.setEndDate("2015-09-28 08:00 +0200");
        triggerInformation.setInterval(100);
        triggerInformation.setIgnoreFiresignorePastFiresAtStart(false);

        when(taskService.findTasksByName(taskName)).thenReturn(Arrays.asList(task));
        when(task.getTrigger()).thenReturn(triggerInformation);

        schedulerTaskTriggerUtil.scheduleTriggerJob(BASE_SUBJECT + "repeatingJob." + taskName);

        ArgumentCaptor<RepeatingSchedulableJob> captor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).safeScheduleRepeatingJob(captor.capture());

        RepeatingSchedulableJob actual = captor.getValue();

        assertEquals(BASE_SUBJECT + "repeatingJob." + taskName, actual.getMotechEvent().getSubject());
        assertEquals(actual.getStartTime(), DATE_FORMAT.parseDateTime(triggerInformation.getStartDate()).toDate());
        assertEquals(actual.getEndTime(), DATE_FORMAT.parseDateTime(triggerInformation.getEndDate()).toDate());
        assertEquals((long) actual.getRepeatIntervalInSeconds(), 100L);
        assertEquals(actual.isIgnorePastFiresAtStart(), false);
    }

    @Test
    public void shouldScheduleJobForCronTrigger() {
        taskName = "Cron Task Test";
        triggerInformation = new SchedulerTaskTriggerInformation("scheduler.cronJobTrigger", CHANNEL, MODULE_NAME,
                VERSION, BASE_SUBJECT + "cronJob.", BASE_SUBJECT + "cronJob." + taskName);
        triggerInformation.setType(SchedulerTaskTriggerInformation.SchedulerJobType.CRON_JOB);
        triggerInformation.setStartDate("2015-09-25 08:00 +0200");
        triggerInformation.setEndDate("2015-09-28 08:00 +0200");
        triggerInformation.setCronExpression("0 15 10 * * ?");
        triggerInformation.setIgnoreFiresignorePastFiresAtStart(false);

        when(taskService.findTasksByName(taskName)).thenReturn(Arrays.asList(task));
        when(task.getTrigger()).thenReturn(triggerInformation);

        schedulerTaskTriggerUtil.scheduleTriggerJob(BASE_SUBJECT + "cronJob." + taskName);

        ArgumentCaptor<CronSchedulableJob> captor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(schedulerService).safeScheduleJob(captor.capture());

        CronSchedulableJob actual = captor.getValue();

        assertEquals(BASE_SUBJECT + "cronJob." + taskName, actual.getMotechEvent().getSubject());
        assertEquals(actual.getStartTime(), DATE_FORMAT.parseDateTime(triggerInformation.getStartDate()).toDate());
        assertEquals(actual.getEndTime(), DATE_FORMAT.parseDateTime(triggerInformation.getEndDate()).toDate());
        assertEquals(actual.getCronExpression(), "0 15 10 * * ?");
        assertEquals(actual.isIgnorePastFiresAtStart(), false);
    }

    @Test
    public void shouldScheduleDayOfWeekTrigger() {
        taskName = "Day of week Task Test";
        triggerInformation = new SchedulerTaskTriggerInformation("scheduler.dayOfWeekTrigger", CHANNEL, MODULE_NAME,
                VERSION, BASE_SUBJECT + "dayOfWeekJob.", BASE_SUBJECT + "dayOfWeekJob." + taskName);
        triggerInformation.setType(SchedulerTaskTriggerInformation.SchedulerJobType.DAY_OF_WEEK_JOB);
        triggerInformation.setStartDate("2015-09-25 08:00 +0200");
        triggerInformation.setEndDate("2015-09-28 08:00 +0200");
        triggerInformation.setDays(Arrays.asList(DayOfWeek.Monday, DayOfWeek.Friday));
        triggerInformation.setTime(new Time(11, 30));
        triggerInformation.setIgnoreFiresignorePastFiresAtStart(false);

        when(taskService.findTasksByName(taskName)).thenReturn(Arrays.asList(task));
        when(task.getTrigger()).thenReturn(triggerInformation);

        schedulerTaskTriggerUtil.scheduleTriggerJob(BASE_SUBJECT + "dayOfWeekJob." + taskName);

        ArgumentCaptor<DayOfWeekSchedulableJob> captor = ArgumentCaptor.forClass(DayOfWeekSchedulableJob.class);
        verify(schedulerService).scheduleDayOfWeekJob(captor.capture());

        DayOfWeekSchedulableJob actual = captor.getValue();

        assertEquals(BASE_SUBJECT + "dayOfWeekJob." + taskName, actual.getMotechEvent().getSubject());
        assertEquals(actual.getStartDate(), DATE_FORMAT.parseDateTime(triggerInformation.getStartDate()).toLocalDate());
        assertEquals(actual.getEndDate(), DATE_FORMAT.parseDateTime(triggerInformation.getEndDate()).toLocalDate());
        assertEquals(2, actual.getCronDays().size());
        // Monday is represented as 2 and Friday as 6
        assertTrue(actual.getCronDays().contains(2));
        assertTrue(actual.getCronDays().contains(6));
        assertEquals(actual.getTime(), new Time(11, 30));
        assertEquals(actual.isIgnorePastFiresAtStart(), false);
    }

    @Test
    public void shouldScheduleRepeatingWithPeriodTrigger() {
        taskName = "Repeating with period Task Test";
        triggerInformation = new SchedulerTaskTriggerInformation("scheduler.repeatingWithPeriodTrigger", CHANNEL, MODULE_NAME,
                VERSION, BASE_SUBJECT + "repeatingJobWithPeriod.", BASE_SUBJECT + "repeatingJobWithPeriod." + taskName);
        triggerInformation.setType(SchedulerTaskTriggerInformation.SchedulerJobType.REPEATING_JOB_WITH_PERIOD_INTERVAL);
        triggerInformation.setStartDate("2015-09-25 08:00 +0200");
        triggerInformation.setEndDate("2015-09-28 08:00 +0200");
        triggerInformation.setRepeatPeriod(new Period(11, 30, 0, 0));
        triggerInformation.setIgnoreFiresignorePastFiresAtStart(false);

        when(taskService.findTasksByName(taskName)).thenReturn(Arrays.asList(task));
        when(task.getTrigger()).thenReturn(triggerInformation);

        schedulerTaskTriggerUtil.scheduleTriggerJob(BASE_SUBJECT + "repeatingJobWithPeriod." + taskName);

        ArgumentCaptor<RepeatingPeriodSchedulableJob> captor = ArgumentCaptor.forClass(RepeatingPeriodSchedulableJob.class);
        verify(schedulerService).safeScheduleRepeatingPeriodJob(captor.capture());

        RepeatingPeriodSchedulableJob actual = captor.getValue();

        assertEquals(BASE_SUBJECT + "repeatingJobWithPeriod." + taskName, actual.getMotechEvent().getSubject());
        assertEquals(actual.getStartTime(), DATE_FORMAT.parseDateTime(triggerInformation.getStartDate()).toDate());
        assertEquals(actual.getEndTime(), DATE_FORMAT.parseDateTime(triggerInformation.getEndDate()).toDate());
        assertEquals(actual.getRepeatPeriod(), new Period(11, 30, 0, 0));
        assertEquals(actual.isIgnorePastFiresAtStart(), false);
    }

    @Test
    public void shouldProperlyUnscheduleRunOnceJob() {
        taskName = "Unschedule Test";
        triggerInformation = new SchedulerTaskTriggerInformation("scheduler.trigger", CHANNEL, MODULE_NAME,
                VERSION, BASE_SUBJECT + "trigger.", BASE_SUBJECT + "trigger." + taskName);
        triggerInformation.setType(SchedulerTaskTriggerInformation.SchedulerJobType.RUN_ONCE_JOB);

        when(task.getTrigger()).thenReturn(triggerInformation);

        schedulerTaskTriggerUtil.unscheduleTaskTrigger(task);

        ArgumentCaptor<JobId> captor = ArgumentCaptor.forClass(JobId.class);
        verify(schedulerService).unscheduleJob(captor.capture());

        JobId actual = captor.getValue();
        assertTrue(actual instanceof RunOnceJobId);
        assertEquals(actual.value(), BASE_SUBJECT + "trigger." + taskName + "-null-runonce");
    }

    @Test
    public void shouldProperlyUnscheduleRepeatingJob() {
        taskName = "Unschedule Test";
        triggerInformation = new SchedulerTaskTriggerInformation("scheduler.trigger", CHANNEL, MODULE_NAME,
                VERSION, BASE_SUBJECT + "trigger.", BASE_SUBJECT + "trigger." + taskName);
        triggerInformation.setType(SchedulerTaskTriggerInformation.SchedulerJobType.REPEATING_JOB);

        when(task.getTrigger()).thenReturn(triggerInformation);

        schedulerTaskTriggerUtil.unscheduleTaskTrigger(task);

        ArgumentCaptor<JobId> captor = ArgumentCaptor.forClass(JobId.class);
        verify(schedulerService).unscheduleJob(captor.capture());

        JobId actual = captor.getValue();
        assertTrue(actual instanceof RepeatingJobId);
        assertEquals(actual.value(), BASE_SUBJECT + "trigger." + taskName + "-null-repeat");
    }

    @Test
    public void shouldProperlyUnscheduleCronJob() {
        taskName = "Unschedule Test";
        triggerInformation = new SchedulerTaskTriggerInformation("scheduler.trigger", CHANNEL, MODULE_NAME,
                VERSION, BASE_SUBJECT + "trigger.", BASE_SUBJECT + "trigger." + taskName);
        triggerInformation.setType(SchedulerTaskTriggerInformation.SchedulerJobType.CRON_JOB);

        when(task.getTrigger()).thenReturn(triggerInformation);

        schedulerTaskTriggerUtil.unscheduleTaskTrigger(task);

        ArgumentCaptor<JobId> captor = ArgumentCaptor.forClass(JobId.class);
        verify(schedulerService).unscheduleJob(captor.capture());

        JobId actual = captor.getValue();
        assertTrue(actual instanceof CronJobId);
        assertEquals(actual.value(), BASE_SUBJECT + "trigger." + taskName + "-null");
    }

    @Test
    public void shouldProperlyUnscheduleDayOfWeekJob() {
        taskName = "Unschedule Test";
        triggerInformation = new SchedulerTaskTriggerInformation("scheduler.trigger", CHANNEL, MODULE_NAME,
                VERSION, BASE_SUBJECT + "trigger.", BASE_SUBJECT + "trigger." + taskName);
        triggerInformation.setType(SchedulerTaskTriggerInformation.SchedulerJobType.DAY_OF_WEEK_JOB);

        when(task.getTrigger()).thenReturn(triggerInformation);

        schedulerTaskTriggerUtil.unscheduleTaskTrigger(task);

        ArgumentCaptor<JobId> captor = ArgumentCaptor.forClass(JobId.class);
        verify(schedulerService).unscheduleJob(captor.capture());

        JobId actual = captor.getValue();
        // Day of Week job is respresented as Cron job and therefore uses Cron Job ID
        assertTrue(actual instanceof CronJobId);
        assertEquals(actual.value(), BASE_SUBJECT + "trigger." + taskName + "-null");
    }
}
