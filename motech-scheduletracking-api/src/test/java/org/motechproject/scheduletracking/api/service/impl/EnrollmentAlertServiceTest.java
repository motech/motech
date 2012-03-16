package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
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
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.joda.time.DateTimeConstants.MILLIS_PER_DAY;
import static org.joda.time.DateTimeConstants.MILLIS_PER_HOUR;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.*;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.*;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(DateUtil.class)
@RunWith(PowerMockRunner.class)
public class EnrollmentAlertServiceTest {

    private EnrollmentAlertService enrollmentAlertService;

    @Mock
    private MotechSchedulerService schedulerService;

    @Before
    public void setup() {
        initMocks(this);

        DateTime now = new DateTime(2012, 3, 16, 8, 15, 0, 0);
        spy(DateUtil.class);
        given(DateUtil.now()).willReturn(now);
        given(DateUtil.today()).willReturn(now.toLocalDate());

        enrollmentAlertService = new EnrollmentAlertService(schedulerService);
    }

    @Test
    public void shouldScheduleOneJobIfThereIsOnlyOneAlertInTheMilestone() {
        String externalId = "entity_1";
        String scheduleName = "my_schedule";

        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(22));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        Enrollment enrollment = new Enrollment(externalId, scheduleName, milestone.getName(), weeksAgo(0), weeksAgo(0), new Time(8, 20), EnrollmentStatus.ACTIVE, schedule);

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(1)).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getAllValues().get(0);
        assertJobDetails(job, String.format("%s.0", enrollment.getId()), newDateTime(weeksAfter(0).toLocalDate(), new Time(8, 20)).toDate(), 2, MILLIS_PER_DAY);
        assertEventDetails(new MilestoneEvent(job.getMotechEvent()), externalId, scheduleName, MilestoneAlert.fromMilestone(milestone, enrollment.getReferenceDateTime()), WindowName.earliest.name());
    }

    @Test
    public void shouldScheduleJobsForMilestoneWithWindowsInHours() {
        String externalId = "entity_1";
        String scheduleName = "my_schedule";

        Milestone milestone = new Milestone("milestone", hours(3), weeks(1).plus(hours(4)), hours(1), hours(5));
        milestone.addAlert(WindowName.earliest, new Alert(hours(1), hours(1), 2, 0));
        milestone.addAlert(WindowName.due, new Alert(weeks(1), hours(2), 2, 1));
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        DateTime now = DateUtil.now();
        Enrollment enrollment = new Enrollment(externalId, scheduleName, milestone.getName(), now, now, null, EnrollmentStatus.ACTIVE, schedule);

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(2)).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getAllValues().get(0);
        assertJobDetails(job, String.format("%s.0", enrollment.getId()), now.plusHours(1).toDate(), 1, MILLIS_PER_HOUR);
        assertEventDetails(new MilestoneEvent(job.getMotechEvent()), externalId, scheduleName, MilestoneAlert.fromMilestone(milestone, enrollment.getReferenceDateTime()), WindowName.earliest.name());

        job = repeatJobCaptor.getAllValues().get(1);
        assertJobDetails(job, String.format("%s.1", enrollment.getId()), now.plusHours(3).plusWeeks(1).toDate(), 1, 2 * MILLIS_PER_HOUR);
        assertEventDetails(new MilestoneEvent(job.getMotechEvent()), externalId, scheduleName, MilestoneAlert.fromMilestone(milestone, enrollment.getReferenceDateTime()), WindowName.due.name());
    }

    @Test
    public void shouldScheduleOneRepeatJobForEachAlertInTheFirstMilestone() {
        String externalId = "entity_1";
        String scheduleName = "my_schedule";

        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(22));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        milestone.addAlert(WindowName.due, new Alert(days(0), days(3), 2, 1));
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        Enrollment enrollment = new Enrollment(externalId, scheduleName, milestone.getName(), weeksAgo(0), weeksAgo(0), new Time(8, 20), EnrollmentStatus.ACTIVE, schedule);

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(2)).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getAllValues().get(0);
        assertJobDetails(job, String.format("%s.0", enrollment.getId()), newDateTime(weeksAfter(0).toLocalDate(), new Time(8, 20)).toDate(), 2, MILLIS_PER_DAY);
        assertEventDetails(new MilestoneEvent(job.getMotechEvent()), externalId, scheduleName, MilestoneAlert.fromMilestone(milestone, enrollment.getReferenceDateTime()), WindowName.earliest.name());

        job = repeatJobCaptor.getAllValues().get(1);
        assertJobDetails(job, String.format("%s.1", enrollment.getId()), newDateTime(weeksAfter(1).toLocalDate(), new Time(8, 20)).toDate(), 1, 3 * MILLIS_PER_DAY);
        assertEventDetails(new MilestoneEvent(job.getMotechEvent()), externalId, scheduleName, MilestoneAlert.fromMilestone(milestone, enrollment.getReferenceDateTime()), WindowName.due.name());
    }

    @Test
    public void shouldPassMilestoneAlertAsPayloadWhileSchedulingTheJob() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(22));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", weeksAgo(0), weeksAgo(0), new Time(8, 20), EnrollmentStatus.ACTIVE, schedule);

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        MilestoneAlert milestoneAlert = new MilestoneEvent(repeatJobCaptor.getValue().getMotechEvent()).getMilestoneAlert();
        assertEquals(weeksAfter(0), milestoneAlert.getEarliestDateTime());
        assertEquals(weeksAfter(1), milestoneAlert.getDueDateTime());
        assertEquals(weeksAfter(2), milestoneAlert.getLateDateTime());
        assertEquals(weeksAfter(3), milestoneAlert.getDefaultmentDateTime());
    }

    @Test
    public void shouldNotScheduleJobsForElapsedAlerts() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(3), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", daysAgo(4), daysAgo(0), new Time(8, 20), EnrollmentStatus.ACTIVE, schedule);

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        RepeatingSchedulableJob job = expectAndCaptureRepeatingJob();
        assertEquals(newDateTime(daysAfter(2).toLocalDate(), new Time(8, 20)).toDate(), job.getStartTime());

        assertRepeatIntervalValue(MILLIS_PER_DAY * 3, job.getRepeatInterval());
        assertEquals(0, job.getRepeatCount().intValue());
    }

    @Test
    public void alertIsElapsedTodayIfItIsBeforePreferredAlertTime() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(3), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);

        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", daysAgo(0), daysAgo(0), new Time(8, 10), EnrollmentStatus.ACTIVE, schedule);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        RepeatingSchedulableJob job = expectAndCaptureRepeatingJob();
        assertEquals(newDateTime(daysAfter(3).toLocalDate(), new Time(8, 10)).toDate(), job.getStartTime());

        assertEquals(1, job.getRepeatCount().intValue());
    }

    @Test
    public void alertIsNotElapsedTodayIfItIsNotBeforePreferredAlertTime() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(3), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);

        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", daysAgo(0), daysAgo(0), new Time(8, 20), EnrollmentStatus.ACTIVE, schedule);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        RepeatingSchedulableJob job = expectAndCaptureRepeatingJob();
        assertEquals(newDateTime(daysAfter(0).toLocalDate(), new Time(8, 20)).toDate(), job.getStartTime());
        assertEquals(2, job.getRepeatCount().intValue());
    }

    @Test
    public void shouldNotScheduleJobsIfAllAlertsHaveElapsed() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(3), 1, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);

        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", daysAgo(4), daysAgo(0), new Time(8, 20), EnrollmentStatus.ACTIVE, schedule);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        verify(schedulerService, times(0)).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldScheduleAlertJobWithOffset() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.due, new Alert(days(3), days(1), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", weeksAgo(0), weeksAgo(0), new Time(8, 20), EnrollmentStatus.ACTIVE, schedule);

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        RepeatingSchedulableJob job = expectAndCaptureRepeatingJob();
        assertEquals(newDateTime(daysAfter(10).toLocalDate(), new Time(8, 20)).toDate(), job.getStartTime());
    }

    @Test
    public void shouldScheduleJobsOfSecondMilestoneBasedOnReferenceDateIfTheScheduleIsBasedOnAbsoluteWindows() {
        String scheduleName = "my_schedule";
        String secondMilestoneName = "milestone_2";

        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone secondMilestone = new Milestone(secondMilestoneName, weeks(5), weeks(3), weeks(3), weeks(3));
        secondMilestone.addAlert(WindowName.due, new Alert(days(0), days(1), 1, 0));

        Schedule schedule = new Schedule(scheduleName);
        schedule.isBasedOnAbsoluteWindows(true);
        schedule.addMilestones(firstMilestone);
        schedule.addMilestones(secondMilestone);

        Enrollment enrollmentIntoSecondMilestone = new Enrollment("some_id", scheduleName, secondMilestoneName, weeksAgo(1), weeksAgo(0), new Time(14, 0), EnrollmentStatus.ACTIVE, schedule);
        enrollmentIntoSecondMilestone.fulfillCurrentMilestone(DateUtil.now());
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollmentIntoSecondMilestone);

        RepeatingSchedulableJob job = expectAndCaptureRepeatingJob();
        assertEquals(newDateTime(weeksAfter(4).toLocalDate(), new Time(14, 0)).toDate(), job.getStartTime());
    }

    @Test
    public void shouldNotScheduleJobsForFutureMilestones() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone secondMilestone = new Milestone("milestone_2", weeks(1), weeks(1), weeks(1), weeks(1));
        secondMilestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", weeksAgo(0), weeksAgo(0), new Time(8, 20), EnrollmentStatus.ACTIVE, schedule);

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        verify(schedulerService, times(0)).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldNotScheduleJobsForPassedWindowInTheFirstMilestone() {
        Milestone milestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 4, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone_1", weeksAgo(1), weeksAgo(0), new Time(8, 20), EnrollmentStatus.ACTIVE, schedule);

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);
        verify(schedulerService, times(0)).scheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldNotScheduleJobsForPassedMilestones() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        firstMilestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 4, 0));
        Milestone secondMilestone = new Milestone("milestone_2", weeks(1), weeks(1), weeks(1), weeks(1));
        secondMilestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 2, 1));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone_2", weeksAgo(4), weeksAgo(0), new Time(8, 20), EnrollmentStatus.ACTIVE, schedule);

        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        RepeatingSchedulableJob job = expectAndCaptureRepeatingJob();
        assertEquals(String.format("%s.1", enrollment.getId()), job.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        assertEquals(newDateTime(weeksAgo(0).toLocalDate(), new Time(8, 20)).toDate(), job.getStartTime());
        assertRepeatIntervalValue(MILLIS_PER_DAY, job.getRepeatInterval());
        assertEquals(1, job.getRepeatCount().intValue());
    }

    public void shouldUnenrollEntityFromTheSchedule() {
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", weeksAgo(4), weeksAgo(4), new Time(8, 20), EnrollmentStatus.ACTIVE, null);
        enrollment.setId("enrollment_1");
        enrollmentAlertService.unscheduleAllAlerts(enrollment);

        verify(schedulerService).safeUnscheduleAllJobs(String.format("%s-%s", EventSubjects.MILESTONE_ALERT, "enrollment_1"));
    }

    private void assertRepeatIntervalValue(long expected, long actual) {
        assertTrue(actual > 0);
        assertEquals(expected, actual);
    }

    private void assertJobDetails(RepeatingSchedulableJob job, String jobIdKey, Date startTime, int repeatCount, int repeatInterval) {
        assertEquals(jobIdKey, job.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        assertEquals(startTime, job.getStartTime());
        assertEquals(repeatCount, job.getRepeatCount().intValue());
        assertRepeatIntervalValue(repeatInterval, job.getRepeatInterval());
    }

    private void assertEventDetails(MilestoneEvent event, String externalId, String scheduleName, MilestoneAlert milestoneAlert, String windowName) {
        assertEquals(externalId, event.getExternalId());
        assertEquals(scheduleName, event.getScheduleName());
        assertEquals(milestoneAlert, event.getMilestoneAlert());
        assertEquals(windowName, event.getWindowName());
    }

    private RepeatingSchedulableJob expectAndCaptureRepeatingJob() {
        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        return repeatJobCaptor.getValue();
    }
}
