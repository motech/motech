package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.joda.time.DateTimeConstants.MILLIS_PER_DAY;
import static org.joda.time.DateTimeConstants.MILLIS_PER_WEEK;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.*;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.valueobjects.factory.WallTimeFactory.wallTime;
import static org.powermock.api.mockito.PowerMockito.spy;

@PrepareForTest(DateUtil.class)
@RunWith(PowerMockRunner.class)
public class EnrollmentAlertServiceTest {

//    @Rule
//    public PowerMockRule rule = new PowerMockRule();

    private EnrollmentAlertService enrollmentAlertService;
    private DateTime now;

    @Mock
    private AllTrackedSchedules allTrackedSchedules;
    @Mock
    private MotechSchedulerService schedulerService;


    @Before
    public void setup() {
        initMocks(this);

        now = new DateTime(2012, 2, 20, 8, 15, 0, 0);
        spy(DateUtil.class);
        given(DateUtil.now()).willReturn(now);
        given(DateUtil.today()).willReturn(now.toLocalDate());

        enrollmentAlertService = new EnrollmentAlertService(allTrackedSchedules, schedulerService);
    }

    @Test
    public void shouldScheduleOneRepeatJobForEachAlertInTheFirstMilestone() {
        String externalId = "entity_1";
        String scheduleName = "my_schedule";

        Milestone milestone = new Milestone("milestone", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(25));
        milestone.addAlert(WindowName.earliest, new Alert(wallTime("0 days"), wallTime("1 day"), 3, 0));
        milestone.addAlert(WindowName.due, new Alert(wallTime("0 days"), wallTime("3 days"), 2, 1));
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);
        Enrollment enrollment = new Enrollment(externalId, scheduleName, milestone.getName(), weeksAgo(0), weeksAgo(0), new Time(8, 20));

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(2)).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getAllValues().get(0);
        assertEquals(String.format("%s.0", enrollment.getId()), job.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        assertEquals(newDateTime(weeksAfter(0), new Time(8, 20)).toDate(), job.getStartTime());
        assertRepeatIntervalValue(MILLIS_PER_DAY, job.getRepeatInterval());
        assertEquals(2, job.getRepeatCount().intValue());

        MilestoneEvent event = new MilestoneEvent(job.getMotechEvent());
        assertEquals(externalId, event.getExternalId());
        assertEquals(scheduleName, event.getScheduleName());
        assertEquals(MilestoneAlert.fromMilestone(milestone, enrollment.getReferenceDate()), event.getMilestoneAlert());
        assertEquals("earliest", event.getWindowName());

        job = repeatJobCaptor.getAllValues().get(1);
        event = new MilestoneEvent(job.getMotechEvent());
        assertEquals(String.format("%s.1", enrollment.getId()), job.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        assertEquals(newDateTime(weeksAfter(1), new Time(8, 20)).toDate(), job.getStartTime());
        assertRepeatIntervalValue(3 * MILLIS_PER_DAY, job.getRepeatInterval());
        assertEquals(1, job.getRepeatCount().intValue());

        assertEquals(externalId, event.getExternalId());
        assertEquals(scheduleName, event.getScheduleName());
        assertEquals(MilestoneAlert.fromMilestone(milestone, enrollment.getReferenceDate()), event.getMilestoneAlert());
        assertEquals("due", event.getWindowName());
    }

    @Test
    public void shouldNotScheduleJobsForElapsedAlerts() {
        Milestone milestone = new Milestone("milestone", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        milestone.addAlert(WindowName.earliest, new Alert(wallTime("0 days"), wallTime("3 days"), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", daysAgo(4), daysAgo(0), new Time(8, 20));

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getValue();
        assertEquals(newDateTime(daysAfter(2), new Time(8, 20)).toDate(), job.getStartTime());
        assertRepeatIntervalValue(MILLIS_PER_DAY * 3, job.getRepeatInterval());
        assertEquals(0, job.getRepeatCount().intValue());
    }

    @Test
    public void alertIsElapsedTodayIfItIsBeforePreferredAlertTime() {
        Milestone milestone = new Milestone("milestone", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        milestone.addAlert(WindowName.earliest, new Alert(wallTime("0 days"), wallTime("3 days"), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", daysAgo(0), daysAgo(0), new Time(8, 10));
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getValue();
        assertEquals(newDateTime(daysAfter(3), new Time(8, 10)).toDate(), job.getStartTime());
        assertEquals(1, job.getRepeatCount().intValue());
    }

    @Test
    public void alertIsNotElapsedTodayIfItIsNotBeforePreferredAlertTime() {
        Milestone milestone = new Milestone("milestone", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        milestone.addAlert(WindowName.earliest, new Alert(wallTime("0 days"), wallTime("3 days"), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", daysAgo(0), daysAgo(0), new Time(8, 20));
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getValue();
        assertEquals(newDateTime(daysAfter(0), new Time(8, 20)).toDate(), job.getStartTime());
        assertEquals(2, job.getRepeatCount().intValue());
    }

    @Test
    public void defectCase1_shouldScheduleLateAlertFrom6thFeb() {
        now = new DateTime(2012, 2, 1, 8, 15, 0, 0);
        LocalDate ref = new LocalDate(2011, 4, 18);
        given(DateUtil.now()).willReturn(now);
        given(DateUtil.today()).willReturn(now.toLocalDate());

        Milestone milestone = new Milestone("milestone", wallTimeOf(0), wallTimeOf(40), wallTimeOf(43), wallTimeOf(44));
        milestone.addAlert(WindowName.due, new Alert(wallTime("1 week"), wallTime("40 weeks"), 1, 0));
        milestone.addAlert(WindowName.late, new Alert(wallTime("1 week"), wallTime("1 week"), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", ref, now.toLocalDate(), new Time(8, 20));
        System.out.println(String.format("%s %s", enrollment.getReferenceDate(), enrollment.getEnrollmentDate()));
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getValue();
        assertEquals(newDateTime(new LocalDate(2012, 2, 6), new Time(8, 20)).toDate(), job.getStartTime());
        assertEquals(1, job.getRepeatCount().intValue());
        assertEquals(7 * MILLIS_PER_DAY, job.getRepeatInterval());
    }

    @Test
    public void defectCase2() {
        Milestone milestone = new Milestone("milestone", wallTimeOf(12), wallTimeOf(13), wallTimeOf(15), wallTimeOf(18));
        milestone.addAlert(WindowName.earliest, new Alert(wallTime("0 weeks"), wallTime("2 days"), 5, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", new LocalDate(2012, 2, 16), now.toLocalDate(), new Time(8, 20));
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getValue();
        assertEquals(newDateTime(new LocalDate(2012, 2, 20), new Time(8, 20)).toDate(), job.getStartTime());
        assertEquals(2, job.getRepeatCount().intValue());
        assertEquals(2 * (long) MILLIS_PER_DAY, job.getRepeatInterval());
    }

    @Test
    public void defectCase3_shouldNotScheduleTheOnlyElapsedAlert() {
        Milestone milestone = new Milestone("milestone", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        milestone.addAlert(WindowName.earliest, new Alert(wallTime("0 days"), wallTime("3 days"), 1, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", daysAgo(4), daysAgo(0), new Time(8, 20));
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(0)).safeScheduleRepeatingJob(repeatJobCaptor.capture());
    }

    @Test
    public void shouldScheduleAlertJobWithOffset() {
        Milestone milestone = new Milestone("milestone", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        milestone.addAlert(WindowName.due, new Alert(wallTime("3 days"), wallTime("1 day"), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", weeksAgo(0), weeksAgo(0), new Time(8, 20));

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getValue();
        assertEquals(newDateTime(daysAfter(10), new Time(8, 20)).toDate(), job.getStartTime());
    }

    @Test
    public void shouldNotScheduleJobsForFutureMilestones() {
        Milestone firstMilestone = new Milestone("milestone_1", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone secondMilestone = new Milestone("milestone_2", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        secondMilestone.addAlert(WindowName.earliest, new Alert(wallTime("0 days"), wallTime("1 day"), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", weeksAgo(0), weeksAgo(0), new Time(8, 20));

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        verify(schedulerService, times(0)).scheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldNotScheduleJobsForPassedWindowInTheFirstMilestone() {
        Milestone milestone = new Milestone("milestone_1", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        milestone.addAlert(WindowName.earliest, new Alert(wallTime("0 days"), wallTime("1 day"), 4, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone_1", weeksAgo(1), weeksAgo(0), new Time(8, 20));

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);
        verify(schedulerService, times(0)).scheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldNotScheduleJobsForPassedMilestones() {
        Milestone firstMilestone = new Milestone("milestone_1", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        firstMilestone.addAlert(WindowName.earliest, new Alert(wallTime("0 days"), wallTime("1 day"), 4, 0));
        Milestone secondMilestone = new Milestone("milestone_2", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        secondMilestone.addAlert(WindowName.earliest, new Alert(wallTime("0 days"), wallTime("1 day"), 2, 1));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone_2", weeksAgo(4), weeksAgo(0), new Time(8, 20));

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getValue();
        assertEquals(String.format("%s.1", enrollment.getId()), job.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        assertEquals(newDateTime(weeksAgo(0), new Time(8, 20)).toDate(), job.getStartTime());
        assertRepeatIntervalValue(MILLIS_PER_DAY, job.getRepeatInterval());
        assertEquals(1, job.getRepeatCount().intValue());
    }

    @Test
    public void shouldReturnReferenceDateWhenCurrentMilestoneIsTheFirstMilestone() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), new Time(8, 20));

        assertEquals(weeksAgo(5), enrollmentAlertService.getCurrentMilestoneStartDate(enrollment));
    }

    @Test
    public void shouldReturnEnrollmentDateWhenEnrolledIntoSecondMilestoneAndNoMilestonesFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", secondMilestone.getName(), weeksAgo(5), weeksAgo(3), null);

        assertEquals(weeksAgo(3), enrollmentAlertService.getCurrentMilestoneStartDate(enrollment));
    }

    @Test
    public void shouldUnenrollEntityFromTheSchedule() {
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", weeksAgo(4), weeksAgo(4), new Time(8, 20));
        enrollment.setId("enrollment_1");
        enrollmentAlertService.unscheduleAllAlerts(enrollment);

        verify(schedulerService).unscheduleAllJobs(String.format("%s-%s", EventSubject.MILESTONE_ALERT, "enrollment_1"));
    }

    private void assertRepeatIntervalValue(long expected, long actual) {
        assertTrue(actual > 0);
        assertEquals(expected, actual);
    }
}
