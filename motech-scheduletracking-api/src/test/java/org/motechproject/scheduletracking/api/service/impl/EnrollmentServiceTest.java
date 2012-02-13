package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.domain.exception.DefaultedMilestoneFulfillmentException;
import org.motechproject.scheduletracking.api.domain.exception.NoMoreMilestonesToFulfillException;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.*;

public class EnrollmentServiceTest {

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
        LocalDate referenceDate = weeksAgo(0);
        LocalDate enrollmentDate = weeksAgo(0);
        Time preferredAlertTime = new Time(8, 10);

        Milestone milestone = new Milestone("milestone", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        milestone.addAlert(WindowName.earliest, new Alert(new WallTime(0, null), new WallTime(1, WallTimeUnit.Day), 3, 0));
        milestone.addAlert(WindowName.due, new Alert(new WallTime(0, null), new WallTime(1, WallTimeUnit.Week), 2, 1));
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);
        Enrollment dummyEnrollment = new Enrollment(externalId, scheduleName, milestone.getName(), referenceDate, enrollmentDate, preferredAlertTime);
        dummyEnrollment.setId("enrollmentId");
        when(allEnrollments.addOrReplace(any(Enrollment.class))).thenReturn(dummyEnrollment);

        enrollmentService.enroll(externalId, scheduleName, milestone.getName(), referenceDate, enrollmentDate, preferredAlertTime);

        ArgumentCaptor<Enrollment> enrollmentArgumentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(allEnrollments).addOrReplace(enrollmentArgumentCaptor.capture());
        assertEnrollment(enrollmentArgumentCaptor.getValue(), externalId, scheduleName, milestone);

        verify(enrollmentAlertService).scheduleAlertsForCurrentMilestone(enrollmentArgumentCaptor.capture());
        assertEnrollment(enrollmentArgumentCaptor.getValue(), externalId, scheduleName, milestone);

        verify(enrollmentDefaultmentService).scheduleJobToCaptureDefaultment(enrollmentArgumentCaptor.getValue());
        assertEnrollment(enrollmentArgumentCaptor.getValue(), externalId, scheduleName, milestone);
    }

    private void assertEnrollment(Enrollment enrollment, String externalId, String scheduleName, Milestone milestone) {
        assertEquals(externalId, enrollment.getExternalId());
        assertEquals(scheduleName, enrollment.getScheduleName());
        assertEquals(milestone.getName(), enrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldFulfillCurrentMilestoneInEnrollment() {
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        secondMilestone.addAlert(WindowName.earliest, new Alert(new WallTime(0, null), wallTimeOf(1), 3, 0));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(4), weeksAgo(4), new Time(8, 20));
        enrollmentService.fulfillCurrentMilestone(enrollment);

        assertEquals("Second Shot", enrollment.getCurrentMilestoneName());
        assertEquals(daysAgo(0), enrollment.getLastFulfilledDate());
    }

    @Test
    public void shouldScheduleJobsForNextMilestoneWhenCurrentMilestoneIsFulfilled() {
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        secondMilestone.addAlert(WindowName.earliest, new Alert(new WallTime(0, null), wallTimeOf(1), 3, 0));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(4), weeksAgo(4), new Time(8, 20));
        enrollment.setId("enrollment_1");
        enrollmentService.fulfillCurrentMilestone(enrollment);

        verify(enrollmentAlertService).unscheduleAllAlerts(enrollment);
        verify(enrollmentDefaultmentService).unscheduleDefaultmentCaptureJob(enrollment);

        ArgumentCaptor<Enrollment> updatedEnrollmentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(enrollmentAlertService).scheduleAlertsForCurrentMilestone(updatedEnrollmentCaptor.capture());
        assertEquals("Second Shot", updatedEnrollmentCaptor.getValue().getCurrentMilestoneName());

        verify(enrollmentDefaultmentService).scheduleJobToCaptureDefaultment(updatedEnrollmentCaptor.capture());
        assertEquals("Second Shot", updatedEnrollmentCaptor.getValue().getCurrentMilestoneName());
    }

    @Test(expected = DefaultedMilestoneFulfillmentException.class)
    public void shouldNotFulfillADefaultedMilestone() {
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        secondMilestone.addAlert(WindowName.earliest, new Alert(new WallTime(0, null), wallTimeOf(1), 3, 0));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(4), weeksAgo(4), new Time(8, 20));
        enrollment.setStatus(EnrollmentStatus.Defaulted);
        enrollmentService.fulfillCurrentMilestone(enrollment);
    }

    @Test(expected = NoMoreMilestonesToFulfillException.class)
    public void shouldThrowExceptionIfAllMilestonesAreFulfilled() {
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(4), weeksAgo(4), new Time(8, 20));
        enrollmentService.fulfillCurrentMilestone(enrollment);
        enrollmentService.fulfillCurrentMilestone(enrollment);
        enrollmentService.fulfillCurrentMilestone(enrollment);

        assertEquals(null, enrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldCompleteEnrollmentWhenAllMilestonesAreFulfilled() {
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(4), weeksAgo(4), new Time(8, 20));
        enrollmentService.fulfillCurrentMilestone(enrollment);
        enrollmentService.fulfillCurrentMilestone(enrollment);

        assertTrue(enrollment.isCompleted());
    }

    @Test
    public void shouldNotHaveAnyJobsScheduledAfterEnrollmentIsComplete() {
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone);
        when(allTrackedSchedules.getByName("Yellow Fever Vaccination")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(4), weeksAgo(4), new Time(8, 20));
        enrollmentService.fulfillCurrentMilestone(enrollment);

        verify(enrollmentAlertService).unscheduleAllAlerts(enrollment);
        verify(enrollmentDefaultmentService).unscheduleDefaultmentCaptureJob(enrollment);
        verify(enrollmentAlertService, times(0)).scheduleAlertsForCurrentMilestone(enrollment);
        verify(enrollmentDefaultmentService, times(0)).scheduleJobToCaptureDefaultment(enrollment);
    }

    @Test
    public void shouldUnenrollEntityFromTheSchedule() {
        Milestone milestone = new Milestone("milestone", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        milestone.addAlert(WindowName.earliest, new Alert(new WallTime(0, null), new WallTime(1, WallTimeUnit.Day), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", weeksAgo(4), weeksAgo(4), new Time(8, 10));
        enrollment.setId("enrollment_1");
        enrollmentService.unenroll(enrollment);

        ArgumentCaptor<Enrollment> enrollmentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(allEnrollments).update(enrollmentCaptor.capture());

        enrollment = enrollmentCaptor.getValue();
        Assert.assertEquals("entity_1", enrollment.getExternalId());
        Assert.assertEquals("my_schedule", enrollment.getScheduleName());
        assertFalse(enrollment.isActive());

        verify(enrollmentAlertService).unscheduleAllAlerts(enrollment);
        verify(enrollmentDefaultmentService).unscheduleDefaultmentCaptureJob(enrollment);
    }
}
