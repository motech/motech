package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.*;
import static org.motechproject.util.DateUtil.newDateTime;

public class EnrollmentServiceTest {

    private static final long MILLIS_IN_A_DAY = 24 * 60 * 60 * 1000L;
    private static final long MILLIS_IN_A_WEEK = MILLIS_IN_A_DAY * 7;

    private EnrollmentService enrollmentService;
    @Mock
    private AllTrackedSchedules allTrackedSchedules;
    @Mock
    private MotechSchedulerService schedulerService;

    @Before
    public void setup() {
        initMocks(this);
        enrollmentService = new EnrollmentService(allTrackedSchedules, schedulerService);
    }

    @Test
    public void shouldScheduleOneRepeatJobForEachAlertInTheFirstMilestone() {
        Milestone milestone = new Milestone("milestone", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        milestone.addAlert(WindowName.earliest, new Alert(new WallTime(1, WallTimeUnit.Day), 3));
        milestone.addAlert(WindowName.due, new Alert(new WallTime(1, WallTimeUnit.Week), 2));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("entity_1", schedule, weeksAgo(0), weeksAgo(0), new Time(8, 10));

        enrollmentService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(2)).scheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getAllValues().get(0);
        assertEquals(String.format("milestone_alert_%s_0", enrollment.getId()), job.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        assertEquals(newDateTime(weeksAgo(0), new Time(8, 10)).toDate(), job.getStartTime());
        assertEquals(MILLIS_IN_A_DAY, job.getRepeatInterval());
        assertEquals(3, job.getRepeatCount().intValue());

        job = repeatJobCaptor.getAllValues().get(1);
        assertEquals(String.format("milestone_alert_%s_1", enrollment.getId()), job.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        assertEquals(newDateTime(weeksAfter(1), new Time(8, 10)).toDate(), job.getStartTime());
        assertEquals(MILLIS_IN_A_WEEK, job.getRepeatInterval());
        assertEquals(2, job.getRepeatCount().intValue());
    }

    @Test
    public void shouldNotScheduleJobsForFutureMilestones() {
        Milestone firstMilestone = new Milestone("milestone_1", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone secondMilestone = new Milestone("milestone_2", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        secondMilestone.addAlert(WindowName.earliest, new Alert(new WallTime(1, WallTimeUnit.Day), 3));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("entity_1", schedule, weeksAgo(0), weeksAgo(0), new Time(8, 10));

        enrollmentService.scheduleAlertsForCurrentMilestone(enrollment);

        verify(schedulerService, times(0)).scheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldNotScheduleJobsInThePast() {
        Milestone milestone = new Milestone("milestone_1", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        milestone.addAlert(WindowName.earliest, new Alert(new WallTime(1, WallTimeUnit.Day), 4));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("entity_1", schedule, daysAgo(6), daysAgo(0), new Time(8, 10));

        enrollmentService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).scheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getValue();
        assertEquals(String.format("milestone_alert_%s_0", enrollment.getId()), job.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        assertEquals(newDateTime(daysAgo(0), new Time(8, 10)).toDate(), job.getStartTime());
        assertEquals(MILLIS_IN_A_DAY, job.getRepeatInterval());
        assertEquals(1, job.getRepeatCount().intValue());
    }

    @Test
    public void shouldNotScheduleJobsForPassedWindowInTheFirstMilestone() {
        Milestone milestone = new Milestone("milestone_1", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        milestone.addAlert(WindowName.earliest, new Alert(new WallTime(1, WallTimeUnit.Day), 4));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("entity_1", schedule, weeksAgo(1), weeksAgo(0), new Time(8, 10));

        enrollmentService.scheduleAlertsForCurrentMilestone(enrollment);
        verify(schedulerService, times(0)).scheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldNotScheduleJobsForPassedMilestones() {
        Milestone firstMilestone = new Milestone("milestone_1", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        firstMilestone.addAlert(WindowName.earliest, new Alert(new WallTime(1, WallTimeUnit.Day), 4));
        Milestone secondMilestone = new Milestone("milestone_2", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        secondMilestone.addAlert(WindowName.earliest, new Alert(new WallTime(1, WallTimeUnit.Day), 2));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("entity_1", schedule, weeksAgo(4), weeksAgo(0), new Time(8, 10), "milestone_2");

        enrollmentService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).scheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getValue();
        assertEquals(String.format("milestone_alert_%s_0", enrollment.getId()), job.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        assertEquals(newDateTime(weeksAgo(0), new Time(8, 10)).toDate(), job.getStartTime());
        assertEquals(MILLIS_IN_A_DAY, job.getRepeatInterval());
        assertEquals(2, job.getRepeatCount().intValue());
    }

    @Test
    public void shouldReturnReferenceDateWhenCurrentMilestoneIsTheFirstMilestone() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, weeksAgo(5), weeksAgo(3), null);

        assertEquals(weeksAgo(5), enrollmentService.getCurrentMilestoneStartDate(enrollment));
    }

    @Test
    public void shouldReturnEnrollmentDateWhenEnrolledIntoSecondMilestoneAndNoMilestonesFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, weeksAgo(5), weeksAgo(3), null, secondMilestone.getName());

        assertEquals(weeksAgo(3), enrollmentService.getCurrentMilestoneStartDate(enrollment));
    }

    @Test
    public void shouldReturnLastFulfilledDateWhenEnrolledIntoSecondMilestoneAndFirstMilestonesFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, weeksAgo(5), weeksAgo(3), null);
        enrollment.fulfillCurrentMilestone(secondMilestone.getName(), weeksAgo(2));

        assertEquals(weeksAgo(2), enrollmentService.getCurrentMilestoneStartDate(enrollment));
    }
}
