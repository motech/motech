package org.motechproject.scheduletracking.api.service.impl;


import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.service.MilestoneAlerts;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.joda.time.DateTimeConstants.MILLIS_PER_DAY;
import static org.joda.time.DateTimeConstants.MILLIS_PER_HOUR;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.*;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.*;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;

public class EnrollmentAlertServiceTest {

    private EnrollmentAlertService enrollmentAlertService;

    @Mock
    private MotechSchedulerService schedulerService;

    @Before
    public void setup() {
        initMocks(this);

        DateTime now = new DateTime(2012, 3, 16, 8, 15, 0, 0);
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        enrollmentAlertService = new EnrollmentAlertService(schedulerService);
    }

    @After
    public void tearDown() throws Exception {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldScheduleOneJobIfThereIsOnlyOneAlertInTheMilestone() {
        String externalId = "entity_1";
        String scheduleName = "my_schedule";

        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(22));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);

        Enrollment enrollment = new Enrollment().setExternalId(externalId).setSchedule(schedule).setCurrentMilestoneName(milestone.getName()).setStartOfSchedule(weeksAgo(0)).setEnrolledOn(weeksAgo(0)).setPreferredAlertTime(new Time(8, 20)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(1)).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getAllValues().get(0);
        assertJobDetails(job, String.format("%s.0", enrollment.getId()), newDateTime(weeksAfter(0).toLocalDate(), new Time(8, 20)).toDate(), 2, MILLIS_PER_DAY);
        assertEventDetails(new MilestoneEvent(job.getMotechEvent()), externalId, scheduleName, MilestoneAlert.fromMilestone(milestone, enrollment.getStartOfSchedule()), WindowName.earliest.name());
    }

    @Test
    public void shouldScheduleJobsForMilestoneWithWindowsInHours() {
        String externalId = "entity_1";
        String scheduleName = "my_schedule";

        Milestone milestone = new Milestone("milestone", hours(3), weeks(1).plus(hours(4)), hours(1), hours(5));
        milestone.addAlert(WindowName.earliest, new Alert(hours(1), hours(1), 2, 0, false));
        milestone.addAlert(WindowName.due, new Alert(weeks(1), hours(2), 2, 1, false));
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        DateTime now = DateUtil.now();

        Enrollment enrollment = new Enrollment().setExternalId(externalId).setSchedule(schedule).setCurrentMilestoneName(milestone.getName()).setStartOfSchedule(now).setEnrolledOn(now).setPreferredAlertTime(null).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(2)).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getAllValues().get(0);
        assertJobDetails(job, String.format("%s.0", enrollment.getId()), now.plusHours(1).toDate(), 1, MILLIS_PER_HOUR);
        assertEventDetails(new MilestoneEvent(job.getMotechEvent()), externalId, scheduleName, MilestoneAlert.fromMilestone(milestone, enrollment.getStartOfSchedule()), WindowName.earliest.name());

        job = repeatJobCaptor.getAllValues().get(1);
        assertJobDetails(job, String.format("%s.1", enrollment.getId()), now.plusHours(3).plusWeeks(1).toDate(), 1, 2 * MILLIS_PER_HOUR);
        assertEventDetails(new MilestoneEvent(job.getMotechEvent()), externalId, scheduleName, MilestoneAlert.fromMilestone(milestone, enrollment.getStartOfSchedule()), WindowName.due.name());
    }

    @Test
    public void shouldScheduleOneRepeatJobForEachAlertInTheFirstMilestone() {
        String externalId = "entity_1";
        String scheduleName = "my_schedule";

        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(22));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        milestone.addAlert(WindowName.due, new Alert(days(0), days(3), 2, 1, false));
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);

        Enrollment enrollment = new Enrollment().setExternalId(externalId).setSchedule(schedule).setCurrentMilestoneName(milestone.getName()).setStartOfSchedule(weeksAgo(0)).setEnrolledOn(weeksAgo(0)).setPreferredAlertTime(new Time(8, 20)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(2)).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        RepeatingSchedulableJob job = repeatJobCaptor.getAllValues().get(0);
        assertJobDetails(job, String.format("%s.0", enrollment.getId()), newDateTime(weeksAfter(0).toLocalDate(), new Time(8, 20)).toDate(), 2, MILLIS_PER_DAY);
        assertEventDetails(new MilestoneEvent(job.getMotechEvent()), externalId, scheduleName, MilestoneAlert.fromMilestone(milestone, enrollment.getStartOfSchedule()), WindowName.earliest.name());

        job = repeatJobCaptor.getAllValues().get(1);
        assertJobDetails(job, String.format("%s.1", enrollment.getId()), newDateTime(weeksAfter(1).toLocalDate(), new Time(8, 20)).toDate(), 1, 3 * MILLIS_PER_DAY);
        assertEventDetails(new MilestoneEvent(job.getMotechEvent()), externalId, scheduleName, MilestoneAlert.fromMilestone(milestone, enrollment.getStartOfSchedule()), WindowName.due.name());
    }

    @Test
    public void shouldPassMilestoneAlertAsPayloadWhileSchedulingTheJob() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(22));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);

        Enrollment enrollment = new Enrollment().setExternalId("entity_1").setSchedule(schedule).setCurrentMilestoneName("milestone").setStartOfSchedule(weeksAgo(0)).setEnrolledOn(weeksAgo(0)).setPreferredAlertTime(new Time(8, 20)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
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
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(3), 3, 0, false));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);

        Enrollment enrollment = new Enrollment().setExternalId("entity_1").setSchedule(schedule).setCurrentMilestoneName("milestone").setStartOfSchedule(daysAgo(4)).setEnrolledOn(daysAgo(0)).setPreferredAlertTime(new Time(8, 20)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        RepeatingSchedulableJob job = expectAndCaptureRepeatingJob();
        assertEquals(newDateTime(daysAfter(2).toLocalDate(), new Time(8, 20)).toDate(), job.getStartTime());

        assertRepeatIntervalValue(MILLIS_PER_DAY * 3, job.getRepeatIntervalInMilliSeconds());
        assertEquals(0, job.getRepeatCount().intValue());
    }

    @Test
    public void alertIsElapsedTodayIfItIsBeforePreferredAlertTime() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(3), 3, 0, false));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);

        Enrollment enrollment = new Enrollment().setExternalId("entity_1").setSchedule(schedule).setCurrentMilestoneName("milestone").setStartOfSchedule(daysAgo(0)).setEnrolledOn(daysAgo(0)).setPreferredAlertTime(new Time(8, 10)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        RepeatingSchedulableJob job = expectAndCaptureRepeatingJob();
        assertEquals(newDateTime(daysAfter(3).toLocalDate(), new Time(8, 10)).toDate(), job.getStartTime());

        assertEquals(1, job.getRepeatCount().intValue());
    }

    @Test
    public void alertIsNotElapsedTodayIfItIsNotBeforePreferredAlertTime() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(3), 3, 0, false));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);

        Enrollment enrollment = new Enrollment().setExternalId("entity_1").setSchedule(schedule).setCurrentMilestoneName("milestone").setStartOfSchedule(daysAgo(0)).setEnrolledOn(daysAgo(0)).setPreferredAlertTime(new Time(8, 20)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        RepeatingSchedulableJob job = expectAndCaptureRepeatingJob();
        assertEquals(newDateTime(daysAfter(0).toLocalDate(), new Time(8, 20)).toDate(), job.getStartTime());
        assertEquals(2, job.getRepeatCount().intValue());
    }

    @Test
    public void shouldNotScheduleAnyJobIfAllAlertsHaveElapsed() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(3), 1, 0, false));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);

        Enrollment enrollment = new Enrollment().setExternalId("entity_1").setSchedule(schedule).setCurrentMilestoneName("milestone").setStartOfSchedule(daysAgo(4)).setEnrolledOn(daysAgo(0)).setPreferredAlertTime(new Time(8, 20)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        verify(schedulerService, never()).safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldScheduleAlertJobWithOffset() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.due, new Alert(days(3), days(1), 3, 0, false));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);

        Enrollment enrollment = new Enrollment().setExternalId("entity_1").setSchedule(schedule).setCurrentMilestoneName("milestone").setStartOfSchedule(weeksAgo(0)).setEnrolledOn(weeksAgo(0)).setPreferredAlertTime(new Time(8, 20)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        RepeatingSchedulableJob job = expectAndCaptureRepeatingJob();
        assertEquals(newDateTime(daysAfter(10).toLocalDate(), new Time(8, 20)).toDate(), job.getStartTime());
    }

    @Test
    public void shouldSceduleJobForAbsoluteSchedule() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone secondMilestone = new Milestone("milestone_2", weeks(1), weeks(1), weeks(1), weeks(1));
        secondMilestone.addAlert(WindowName.due, new Alert(days(0), days(1), 1, 0, false));

        Schedule schedule = new Schedule("my_schedule");
        schedule.isBasedOnAbsoluteWindows(true);
        schedule.addMilestones(firstMilestone, secondMilestone);

        Enrollment enrollmentIntoSecondMilestone = new Enrollment().setExternalId("some_id").setSchedule(schedule).setCurrentMilestoneName("milestone_2").setStartOfSchedule(weeksAgo(0)).setEnrolledOn(weeksAgo(0)).setPreferredAlertTime(new Time(0, 0)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollmentIntoSecondMilestone);

        RepeatingSchedulableJob job = expectAndCaptureRepeatingJob();
        assertEquals(newDateTime(weeksAfter(5).toLocalDate(), new Time(0, 0)).toDate(), job.getStartTime());
    }

    @Test
    public void shouldScheduleJobForFloatingAlerts() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        Alert alert = new Alert(days(0), days(1), 7, 0, true);
        firstMilestone.addAlert(WindowName.due, alert);

        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);

        Enrollment enrollment = new Enrollment().setExternalId("some_id").setSchedule(schedule).setCurrentMilestoneName("milestone_1").setStartOfSchedule(daysAgo(12)).setEnrolledOn(DateUtil.now()).setPreferredAlertTime(new Time(8, 15)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(1)).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        assertEquals(DateUtil.now().toDate(), repeatJobCaptor.getValue().getStartTime());
        assertEquals(1, repeatJobCaptor.getValue().getRepeatCount().intValue());
    }

    @Test
    public void shouldScheduleJobForFloatingAlerts_WithPreferredTimeEarlierThanCurrentTime() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        Alert alert = new Alert(days(0), days(3), 7, 0, true);
        firstMilestone.addAlert(WindowName.due, alert);

        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);

        Enrollment enrollment = new Enrollment().setExternalId("some_id").setSchedule(schedule).setCurrentMilestoneName("milestone_1").setStartOfSchedule(daysAgo(12)).setEnrolledOn(DateUtil.now()).setPreferredAlertTime(new Time(6, 15)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(1)).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        assertEquals(newDateTime(DateUtil.now().plusDays(1).toLocalDate(), new Time(6, 15)).toDate(), repeatJobCaptor.getValue().getStartTime());
        assertEquals(0, repeatJobCaptor.getValue().getRepeatCount().intValue());
    }


    @Test
    public void shouldConsiderZeroOffsetForBackDatedFloatingAlerts() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), months(10), weeks(1), weeks(1));
        Alert alert = new Alert(days(-5), days(1), 7, 0, true);
        firstMilestone.addAlert(WindowName.due, alert);

        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);

        Enrollment enrollment = new Enrollment().setExternalId("some_id").setSchedule(schedule).setCurrentMilestoneName("milestone_1").setStartOfSchedule(daysAgo(30)).setEnrolledOn(DateUtil.now()).setPreferredAlertTime(new Time(8, 15)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(1)).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        assertEquals(DateUtil.now().toDate(), repeatJobCaptor.getValue().getStartTime());
        assertEquals(6, repeatJobCaptor.getValue().getRepeatCount().intValue());
    }

    @Test
    public void shouldConsiderZeroOffsetForBackDatedFloatingAlertsWhenEnrolledInADifferentWindow() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(4), months(10), weeks(0), weeks(0));
        Alert alert = new Alert(weeks(-2), days(1), 1, 0, true);
        firstMilestone.addAlert(WindowName.due, alert);

        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);

        Enrollment enrollment = new Enrollment().setExternalId("some_id").setSchedule(schedule).setCurrentMilestoneName("milestone_1").setStartOfSchedule(weeksAgo(4)).setEnrolledOn(weeksAgo(1)).setPreferredAlertTime(new Time(8, 15)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        ArgumentCaptor<RepeatingSchedulableJob> repeatJobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService, times(1)).safeScheduleRepeatingJob(repeatJobCaptor.capture());

        assertEquals(DateUtil.now().toDate(), repeatJobCaptor.getValue().getStartTime());
        assertEquals(0, repeatJobCaptor.getValue().getRepeatCount().intValue());
    }

    @Test
    public void shouldReturnAlertTimingsForTheGivenMilestone() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        firstMilestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 2, 0, false));
        firstMilestone.addAlert(WindowName.earliest, new Alert(days(2), days(1), 1, 0, false));
        firstMilestone.addAlert(WindowName.due, new Alert(days(1), days(1), 7, 0, false));
        firstMilestone.addAlert(WindowName.max, new Alert(days(2), days(2), 7, 0, false));

        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);

        Enrollment enrollment = new Enrollment().setExternalId("some_id").setSchedule(schedule).setCurrentMilestoneName("milestone_1").setStartOfSchedule(now()).setEnrolledOn(now()).setPreferredAlertTime(new Time(8, 15)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        MilestoneAlerts milestoneAlerts = enrollmentAlertService.getAlertTimings(enrollment);

        List<DateTime> earliestWindowAlertTimings = milestoneAlerts.getEarliestWindowAlertTimings();
        assertEquals(3, earliestWindowAlertTimings.size());
        assertEquals(new DateTime(2012, 3, 16, 8, 15, 0, 0), earliestWindowAlertTimings.get(0));
        assertEquals(new DateTime(2012, 3, 17, 8, 15, 0, 0), earliestWindowAlertTimings.get(1));
        assertEquals(new DateTime(2012, 3, 18, 8, 15, 0, 0), earliestWindowAlertTimings.get(2));

        List<DateTime> dueWindowAlertTimings = milestoneAlerts.getDueWindowAlertTimings();
        assertEquals(7, dueWindowAlertTimings.size());
        assertEquals(new DateTime(2012, 3, 24, 8, 15, 0, 0), dueWindowAlertTimings.get(0));
        assertEquals(new DateTime(2012, 3, 30, 8, 15, 0, 0), dueWindowAlertTimings.get(6));

        List<DateTime> maxWindowAlertTimings = milestoneAlerts.getMaxWindowAlertTimings();
        assertEquals(7, maxWindowAlertTimings.size());
        assertEquals(new DateTime(2012, 4, 8, 8, 15, 0, 0), maxWindowAlertTimings.get(0));
        assertEquals(new DateTime(2012, 4, 20, 8, 15, 0, 0), maxWindowAlertTimings.get(6));
    }

    @Test
    public void shouldReturnAlertTimingsAsNullForTheGivenNonExistingMilestone() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        firstMilestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 2, 0, false));

        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);

        Enrollment enrollment = new Enrollment().setExternalId("some_id").setSchedule(schedule).setCurrentMilestoneName("milestone_non_existent").setStartOfSchedule(now()).setEnrolledOn(now()).setPreferredAlertTime(new Time(8, 15)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        MilestoneAlerts milestoneAlerts = enrollmentAlertService.getAlertTimings(enrollment);

        List<DateTime> earliestWindowAlertTimings = milestoneAlerts.getEarliestWindowAlertTimings();
        assertNull(earliestWindowAlertTimings);
    }

    @Test
    public void shouldReturnAlertTimingsForElapsedAlertsAlsoInTheMilestoneWindow() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        firstMilestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 5, 0, false));

        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);

        Enrollment enrollment = new Enrollment().setExternalId("some_id").setSchedule(schedule).setCurrentMilestoneName("milestone_1").setStartOfSchedule(daysAgo(3)).setEnrolledOn(daysAgo(3)).setPreferredAlertTime(new Time(8, 15)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        MilestoneAlerts milestoneAlerts = enrollmentAlertService.getAlertTimings(enrollment);

        List<DateTime> earliestWindowAlertTimings = milestoneAlerts.getEarliestWindowAlertTimings();
        assertEquals(5, earliestWindowAlertTimings.size());
        assertEquals(new DateTime(2012, 3, 13, 8, 15, 0, 0), earliestWindowAlertTimings.get(0));
        assertEquals(new DateTime(2012, 3, 14, 8, 15, 0, 0), earliestWindowAlertTimings.get(1));
    }

    @Test
    public void shouldReturnAlertTimingsForElapsedWindowAlsoInTheMilestone() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        firstMilestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 5, 0, false));

        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);

        Enrollment enrollment = new Enrollment().setExternalId("some_id").setSchedule(schedule).setCurrentMilestoneName("milestone_1").setStartOfSchedule(weeksAgo(1)).setEnrolledOn(weeksAgo(1)).setPreferredAlertTime(new Time(8, 15)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        MilestoneAlerts milestoneAlerts = enrollmentAlertService.getAlertTimings(enrollment);

        List<DateTime> earliestWindowAlertTimings = milestoneAlerts.getEarliestWindowAlertTimings();
        assertEquals(5, earliestWindowAlertTimings.size());
        assertEquals(new DateTime(2012, 3, 9, 8, 15, 0, 0), earliestWindowAlertTimings.get(0));
        assertEquals(new DateTime(2012, 3, 10, 8, 15, 0, 0), earliestWindowAlertTimings.get(1));
    }

    @Test
    public void shouldReturnAlertTimingsForAbsoluteSchedule() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone secondMilestone = new Milestone("milestone_2", weeks(1), weeks(1), weeks(1), weeks(1));
        secondMilestone.addAlert(WindowName.due, new Alert(days(0), days(1), 3, 0, false));

        Schedule schedule = new Schedule("my_schedule");
        schedule.isBasedOnAbsoluteWindows(true);
        schedule.addMilestones(firstMilestone, secondMilestone);

        Enrollment enrollmentIntoSecondMilestone = new Enrollment().setExternalId("some_id").setSchedule(schedule).setCurrentMilestoneName("milestone_2").setStartOfSchedule(weeksAgo(0)).setEnrolledOn(weeksAgo(0)).setPreferredAlertTime(new Time(0, 0)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        MilestoneAlerts milestoneAlerts = enrollmentAlertService.getAlertTimings(enrollmentIntoSecondMilestone);

        List<DateTime> dueWindowAlertTimings = milestoneAlerts.getDueWindowAlertTimings();
        assertEquals(3, dueWindowAlertTimings.size());
        assertEquals(new DateTime(2012, 4, 20, 0, 0, 0, 0), dueWindowAlertTimings.get(0));
    }

    @Test
    public void shouldReturnAlertTimingsForFloatingAlerts() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        firstMilestone.addAlert(WindowName.due, new Alert(days(0), days(1), 7, 0, true));

        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);

        Enrollment enrollment = new Enrollment().setExternalId("some_id").setSchedule(schedule).setCurrentMilestoneName("milestone_1").setStartOfSchedule(daysAgo(12)).setEnrolledOn(DateUtil.now()).setPreferredAlertTime(new Time(8, 15)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        MilestoneAlerts milestoneAlerts = enrollmentAlertService.getAlertTimings(enrollment);

        List<DateTime> dueWindowAlertTimings = milestoneAlerts.getDueWindowAlertTimings();
        assertEquals(7, dueWindowAlertTimings.size());
        assertEquals(now(), dueWindowAlertTimings.get(0));
    }

    @Test
    public void shouldNotScheduleJobsForFutureMilestones() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone secondMilestone = new Milestone("milestone_2", weeks(1), weeks(1), weeks(1), weeks(1));
        secondMilestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone, secondMilestone);

        Enrollment enrollment = new Enrollment().setExternalId("entity_1").setSchedule(schedule).setCurrentMilestoneName("milestone").setStartOfSchedule(weeksAgo(0)).setEnrolledOn(weeksAgo(0)).setPreferredAlertTime(new Time(8, 20)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        verify(schedulerService, times(0)).
            safeScheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldNotScheduleJobsForPassedWindowInTheFirstMilestone() {
        Milestone milestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 4, 0, false));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);

        Enrollment enrollment = new Enrollment().setExternalId("entity_1").setSchedule(schedule).setCurrentMilestoneName("milestone_1").setStartOfSchedule(weeksAgo(1)).setEnrolledOn(weeksAgo(0)).setPreferredAlertTime(new Time(8, 20)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        verify(schedulerService, times(0)).scheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    @Test
    public void shouldNotScheduleJobsForPassedMilestones() {
        Milestone firstMilestone = new Milestone("milestone_1", weeks(1), weeks(1), weeks(1), weeks(1));
        firstMilestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 4, 0, false));
        Milestone secondMilestone = new Milestone("milestone_2", weeks(1), weeks(1), weeks(1), weeks(1));
        secondMilestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 2, 1, false));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone, secondMilestone);

        Enrollment enrollment = new Enrollment().setExternalId("entity_1").setSchedule(schedule).setCurrentMilestoneName("milestone_2").setStartOfSchedule(weeksAgo(4)).setEnrolledOn(weeksAgo(0)).setPreferredAlertTime(new Time(8, 20)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollmentAlertService.scheduleAlertsForCurrentMilestone(enrollment);

        RepeatingSchedulableJob job = expectAndCaptureRepeatingJob();
        assertEquals(String.format("%s.1", enrollment.getId()), job.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        assertEquals(newDateTime(weeksAgo(0).toLocalDate(), new Time(8, 20)).toDate(), job.getStartTime());
        assertRepeatIntervalValue(MILLIS_PER_DAY, job.getRepeatIntervalInMilliSeconds());
        assertEquals(1, job.getRepeatCount().intValue());
    }

    public void shouldUnenrollEntityFromTheSchedule() {
        Enrollment enrollment = new Enrollment().setExternalId("entity_1").setSchedule(null).setCurrentMilestoneName("milestone").setStartOfSchedule(weeksAgo(4)).setEnrolledOn(weeksAgo(4)).setPreferredAlertTime(new Time(8, 20)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
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
        assertRepeatIntervalValue(repeatInterval, job.getRepeatIntervalInMilliSeconds());
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
