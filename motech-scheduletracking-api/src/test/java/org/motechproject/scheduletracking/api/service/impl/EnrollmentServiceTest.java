package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.domain.exception.NoMoreMilestonesToFulfillException;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.days;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.weeks;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;

public class EnrollmentServiceTest extends BaseUnitTest {
    private EnrollmentService enrollmentService;

    @Mock
    private AllTrackedSchedules allTrackedSchedules;
    @Mock
    private AllEnrollments allEnrollments;
    @Mock
    private EnrollmentAlertService enrollmentAlertService;
    @Mock
    private EnrollmentDefaultmentService enrollmentDefaultmentService;

    @Before
    public void setup() {
        initMocks(this);
        enrollmentService = new EnrollmentService(allTrackedSchedules, allEnrollments, enrollmentAlertService, enrollmentDefaultmentService);
    }

    @Test
    public void shouldEnrollEntityIntoSchedule() {
        String externalId = "entity_1";
        String scheduleName = "my_schedule";
        DateTime referenceDate = weeksAgo(0);
        DateTime enrollmentDate = weeksAgo(0);
        Time preferredAlertTime = new Time(8, 10);

        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        milestone.addAlert(WindowName.due, new Alert(days(0), weeks(1), 2, 1));
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);
        when(allEnrollments.getActiveEnrollment(externalId, scheduleName)).thenReturn(null);

        Enrollment dummyEnrollment = new Enrollment(externalId, schedule, milestone.getName(), referenceDate, enrollmentDate, preferredAlertTime, ACTIVE, null);
        dummyEnrollment.setId("enrollmentId");

        Map<String, String> metadata = new HashMap<String, String>();
        enrollmentService.enroll(externalId, scheduleName, milestone.getName(), referenceDate, enrollmentDate, preferredAlertTime, metadata);

        verify(allEnrollments, times(0)).update(Matchers.<Enrollment>any());
        ArgumentCaptor<Enrollment> enrollmentArgumentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(allEnrollments, times(1)).add(enrollmentArgumentCaptor.capture());
        assertEnrollment(enrollmentArgumentCaptor.getValue(), externalId, scheduleName, milestone, ACTIVE, schedule, metadata);


        verify(enrollmentAlertService).scheduleAlertsForCurrentMilestone(enrollmentArgumentCaptor.capture());
        assertEnrollment(enrollmentArgumentCaptor.getValue(), externalId, scheduleName, milestone, ACTIVE, schedule, metadata);

        verify(enrollmentDefaultmentService).scheduleJobToCaptureDefaultment(enrollmentArgumentCaptor.getValue());
        assertEnrollment(enrollmentArgumentCaptor.getValue(), externalId, scheduleName, milestone, ACTIVE, schedule, metadata);

        verify(enrollmentAlertService, times(0)).unscheduleAllAlerts(Matchers.<Enrollment>any());
        verify(enrollmentDefaultmentService, times(0)).unscheduleDefaultmentCaptureJob(Matchers.<Enrollment>any());
    }

    @Test
    public void shouldEnrollEntityAsDefaultedOneIfMilestoneDurationHasExpired() {
        String externalId = "entity_1";
        String externalId2 = "entity_2";
        String externalId3 = "entity_3";
        String scheduleName = "my_schedule";
        DateTime now = newDateTime(2012, 4, 4, 3, 3, 59);
        DateTime referenceDateTime = now.minusDays(29);
        DateTime enrollmentDateTime = now;
        Time preferredAlertTime = new Time(8, 10);
        EnrollmentStatus enrollmentStatus = EnrollmentStatus.DEFAULTED;

        mockCurrentDate(now);
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone milestone2 = new Milestone("milestone2", weeks(4), weeks(1), weeks(1), weeks(0));
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone, milestone2);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);
        Enrollment dummyEnrollment = new Enrollment(externalId, schedule, milestone.getName(), referenceDateTime, enrollmentDateTime, preferredAlertTime, EnrollmentStatus.ACTIVE, null);
        dummyEnrollment.setId("enrollmentId");
        when(allEnrollments.getActiveEnrollment(externalId, scheduleName)).thenReturn(null);

        enrollmentService.enroll(externalId, scheduleName, milestone.getName(), referenceDateTime, enrollmentDateTime, preferredAlertTime, null);
        DateTime enrollmentDateTimeBeforeMilestone2 = now.minusWeeks(6).minusSeconds(1);
        enrollmentService.enroll(externalId2, scheduleName, milestone2.getName(), null, enrollmentDateTimeBeforeMilestone2, preferredAlertTime, null);
        DateTime enrollmentDateTimeInMilestone2Start = now.minusWeeks(6);
        enrollmentService.enroll(externalId3, scheduleName, milestone2.getName(), null, enrollmentDateTimeInMilestone2Start, preferredAlertTime, null);

        verify(allEnrollments, times(0)).update(Matchers.<Enrollment>any());

        ArgumentCaptor<Enrollment> enrollmentArgumentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(allEnrollments, times(3)).add(enrollmentArgumentCaptor.capture());
        assertEnrollment(enrollmentArgumentCaptor.getAllValues().get(0), externalId, scheduleName, milestone, enrollmentStatus, schedule, null);
        assertEnrollment(enrollmentArgumentCaptor.getAllValues().get(1), externalId2, scheduleName, milestone2, EnrollmentStatus.DEFAULTED, schedule, null);
        assertEnrollment(enrollmentArgumentCaptor.getAllValues().get(2), externalId3, scheduleName, milestone2, EnrollmentStatus.ACTIVE, schedule, null);
    }

    @Test
    public void shouldUnenrollOldEntityIfTheSameEntityIsEnrolledAgain() {
        String externalId = "entity_1";
        String scheduleName = "my_schedule";
        DateTime referenceDate = weeksAgo(0);
        DateTime enrollmentDate = weeksAgo(0);
        Time preferredAlertTime = new Time(8, 10);

        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        milestone.addAlert(WindowName.due, new Alert(days(0), weeks(1), 2, 1));
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);
        Enrollment dummyEnrollment = new Enrollment(externalId, schedule, milestone.getName(), referenceDate, enrollmentDate, preferredAlertTime, ACTIVE, null);
        Enrollment oldEnrollment = new Enrollment(externalId, schedule, "milestone", null, null, null, EnrollmentStatus.ACTIVE, null);
        when(allEnrollments.getActiveEnrollment(externalId, scheduleName)).thenReturn(oldEnrollment);

        dummyEnrollment.setId("enrollmentId");

        Map<String, String> metadata = new HashMap<String, String>();
        enrollmentService.enroll(externalId, scheduleName, milestone.getName(), referenceDate, enrollmentDate, preferredAlertTime, metadata);

        ArgumentCaptor<Enrollment> enrollmentArgumentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(allEnrollments, times(1)).update(enrollmentArgumentCaptor.capture());
        assertEnrollment(enrollmentArgumentCaptor.getValue(), externalId, scheduleName, milestone, ACTIVE, schedule, null);
        verify(allEnrollments, times(0)).add(Matchers.<Enrollment>any());

        verify(enrollmentAlertService).unscheduleAllAlerts(enrollmentArgumentCaptor.capture());
        assertEnrollment(enrollmentArgumentCaptor.getValue(), externalId, scheduleName, new Milestone("milestone", null, null, null, null), EnrollmentStatus.ACTIVE, schedule, null);

        verify(enrollmentDefaultmentService).unscheduleDefaultmentCaptureJob(enrollmentArgumentCaptor.capture());
        assertEnrollment(enrollmentArgumentCaptor.getValue(), externalId, scheduleName, new Milestone("milestone", null, null, null, null), EnrollmentStatus.ACTIVE, schedule, null);

        verify(enrollmentAlertService, times(1)).scheduleAlertsForCurrentMilestone(enrollmentArgumentCaptor.capture());
        assertEnrollment(enrollmentArgumentCaptor.getValue(), externalId, scheduleName, milestone, ACTIVE, schedule, null);

        verify(enrollmentDefaultmentService, times(1)).scheduleJobToCaptureDefaultment(enrollmentArgumentCaptor.getValue());
        assertEnrollment(enrollmentArgumentCaptor.getValue(), externalId, scheduleName, milestone, ACTIVE, schedule, null);

    }

    @Test
    public void shouldFulfillCurrentMilestoneInEnrollment() {
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        secondMilestone.addAlert(WindowName.earliest, new Alert(days(0), weeks(1), 3, 0));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("ID-074285", schedule, "First Shot", weeksAgo(4), weeksAgo(4), new Time(8, 20), ACTIVE, null);
        DateTime now = now();
        enrollmentService.fulfillCurrentMilestone(enrollment, now);

        assertEquals("Second Shot", enrollment.getCurrentMilestoneName());
        assertEquals(now, enrollment.getLastFulfilledDate());

        verify(allEnrollments).update(enrollment);
    }

    @Test
    public void shouldScheduleJobsForNextMilestoneWhenCurrentMilestoneIsFulfilled() {
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        secondMilestone.addAlert(WindowName.earliest, new Alert(days(0), weeks(1), 3, 0));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("ID-074285", schedule, "First Shot", weeksAgo(4), weeksAgo(4), new Time(8, 20), ACTIVE, null);
        enrollment.setId("enrollment_1");
        enrollmentService.fulfillCurrentMilestone(enrollment, null);

        verify(enrollmentAlertService).unscheduleAllAlerts(enrollment);
        verify(enrollmentDefaultmentService).unscheduleDefaultmentCaptureJob(enrollment);

        ArgumentCaptor<Enrollment> updatedEnrollmentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(enrollmentAlertService).scheduleAlertsForCurrentMilestone(updatedEnrollmentCaptor.capture());
        assertEquals("Second Shot", updatedEnrollmentCaptor.getValue().getCurrentMilestoneName());

        verify(enrollmentDefaultmentService).scheduleJobToCaptureDefaultment(updatedEnrollmentCaptor.capture());
        assertEquals("Second Shot", updatedEnrollmentCaptor.getValue().getCurrentMilestoneName());

        verify(allEnrollments).update(enrollment);
    }

    @Test(expected = NoMoreMilestonesToFulfillException.class)
    public void shouldThrowExceptionIfAllMilestonesAreFulfilled() {
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("ID-074285", schedule, "First Shot", weeksAgo(4), weeksAgo(4), new Time(8, 20), ACTIVE, null);
        enrollmentService.fulfillCurrentMilestone(enrollment, null);
        enrollmentService.fulfillCurrentMilestone(enrollment, null);
        enrollmentService.fulfillCurrentMilestone(enrollment, null);

        assertEquals(null, enrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldCompleteEnrollmentWhenAllMilestonesAreFulfilled() {
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        String scheduleName = "Yellow Fever Vaccination";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("ID-074285", schedule, "First Shot", weeksAgo(4), weeksAgo(4), new Time(8, 20), ACTIVE, null);
        enrollmentService.fulfillCurrentMilestone(enrollment, null);
        enrollmentService.fulfillCurrentMilestone(enrollment, null);

        assertEquals(true, enrollment.isCompleted());
        verify(allEnrollments, times(2)).update(enrollment);
    }

    @Test
    public void shouldNotHaveAnyJobsScheduledAfterEnrollmentIsComplete() {
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("ID-074285", schedule, "First Shot", weeksAgo(4), weeksAgo(4), new Time(8, 20), ACTIVE, null);
        enrollmentService.fulfillCurrentMilestone(enrollment, null);

        verify(enrollmentAlertService).unscheduleAllAlerts(enrollment);
        verify(enrollmentDefaultmentService).unscheduleDefaultmentCaptureJob(enrollment);
        verify(enrollmentAlertService, times(0)).scheduleAlertsForCurrentMilestone(enrollment);
        verify(enrollmentDefaultmentService, times(0)).scheduleJobToCaptureDefaultment(enrollment);
    }

    @Test
    public void shouldUnenrollEntityFromTheSchedule() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("entity_1", schedule, "milestone", weeksAgo(4), weeksAgo(4), new Time(8, 10), ACTIVE, null);
        enrollment.setId("enrollment_1");
        enrollmentService.unenroll(enrollment);

        ArgumentCaptor<Enrollment> enrollmentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(allEnrollments).update(enrollmentCaptor.capture());

        enrollment = enrollmentCaptor.getValue();
        Assert.assertEquals("entity_1", enrollment.getExternalId());
        Assert.assertEquals(scheduleName, enrollment.getScheduleName());
        assertEquals(false, enrollment.isActive());

        verify(enrollmentAlertService).unscheduleAllAlerts(enrollment);
        verify(enrollmentDefaultmentService).unscheduleDefaultmentCaptureJob(enrollment);
    }

    @Test
    public void shouldReturnReferenceDateWhenCurrentMilestoneIsTheFirstMilestone() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        DateTime startOfSchedule = weeksAgo(5);
        DateTime enrolledOn = weeksAgo(3);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, "milestone", startOfSchedule, enrolledOn, new Time(8, 20), EnrollmentStatus.ACTIVE, null);
        assertEquals(startOfSchedule, enrollment.getCurrentMilestoneStartDate());
    }

    @Test
    public void shouldReturnEnrollmentDateWhenEnrolledIntoSecondMilestoneAndNoMilestonesFulfilled() {
        Milestone firstMilestone = new Milestone("first_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone secondMilestone = new Milestone("second_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        DateTime expected = weeksAgo(3);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, "second_milestone", weeksAgo(5), expected, null, EnrollmentStatus.ACTIVE, null);
        assertEquals(expected, enrollment.getCurrentMilestoneStartDate());
    }

    @Test
    public void shouldReturnTheWindowAnEnrollmentIsInForTheCurrentMilestone() {
        Milestone firstMilestone = new Milestone("first_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        DateTime referenceDate = newDateTime(2012, 12, 4, 8, 30, 0);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, "first_milestone", referenceDate, referenceDate, null, EnrollmentStatus.ACTIVE, null);

        assertEquals(WindowName.earliest, enrollmentService.getCurrentWindowAsOf(enrollment, newDateTime(2012, 12, 4, 8, 30, 0)));
        assertEquals(WindowName.earliest, enrollmentService.getCurrentWindowAsOf(enrollment, newDateTime(2012, 12, 4, 8, 30, 1)));
        assertEquals(WindowName.due, enrollmentService.getCurrentWindowAsOf(enrollment, newDateTime(2012, 12, 11, 9, 30, 0)));
        assertEquals(WindowName.due, enrollmentService.getCurrentWindowAsOf(enrollment, newDateTime(2012, 12, 11, 8, 30, 0)));
        assertEquals(WindowName.late, enrollmentService.getCurrentWindowAsOf(enrollment, newDateTime(2012, 12, 18, 9, 30, 0)));
        assertEquals(WindowName.max, enrollmentService.getCurrentWindowAsOf(enrollment, newDateTime(2012, 12, 28, 9, 30, 0)));
        assertEquals(null, enrollmentService.getCurrentWindowAsOf(enrollment, newDateTime(2013, 1, 28, 9, 30, 0)));
    }

    @Test
    public void shouldReturnTheEndOfAGivenWindowForTheCurrentMilestone() {
        Milestone firstMilestone = new Milestone("first_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        DateTime referenceDate = newDateTime(2012, 12, 4, 8, 30, 0);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, "first_milestone", referenceDate, referenceDate, null, EnrollmentStatus.ACTIVE, null);

        assertEquals(referenceDate.plusWeeks(1), enrollmentService.getEndOfWindowForCurrentMilestone(enrollment, WindowName.earliest));
        assertEquals(referenceDate.plusWeeks(2), enrollmentService.getEndOfWindowForCurrentMilestone(enrollment, WindowName.due));
        assertEquals(referenceDate.plusWeeks(3), enrollmentService.getEndOfWindowForCurrentMilestone(enrollment, WindowName.late));
        assertEquals(referenceDate.plusWeeks(4), enrollmentService.getEndOfWindowForCurrentMilestone(enrollment, WindowName.max));
    }

    private void assertEnrollment(Enrollment enrollment, String externalId, String scheduleName, Milestone milestone, EnrollmentStatus enrollmentStatus, Schedule schedule, Map<String, String> metadata) {
        assertEquals(externalId, enrollment.getExternalId());
        assertEquals(scheduleName, enrollment.getScheduleName());
        assertEquals(milestone.getName(), enrollment.getCurrentMilestoneName());
        assertEquals(enrollmentStatus, enrollment.getStatus());
        assertEquals(schedule, enrollment.getSchedule());
        assertEquals(metadata, enrollment.getMetadata());
    }
}
