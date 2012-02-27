package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.domain.exception.InvalidEnrollmentException;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentResponse;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.days;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.weeks;
import static org.motechproject.util.DateUtil.today;

public class ScheduleTrackingServiceImplTest {
    @Mock
    private AllTrackedSchedules allTrackedSchedules;
    @Mock
    private MotechSchedulerService schedulerService;
    @Mock
    private AllEnrollments allEnrollments;
    @Mock
    private EnrollmentService enrollmentService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldEnrollEntityIntoFirstMilestoneOfSchedule() {
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        Milestone secondMilestone = new Milestone("second_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("first_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);

        ScheduleTrackingService scheduleTrackingService = new ScheduleTrackingServiceImpl(allTrackedSchedules, allEnrollments, enrollmentService);

        String externalId = "my_entity_1";
        LocalDate referenceDate = new LocalDate(2012, 1, 2);
        Time preferredAlertTime = new Time(8, 10);
        scheduleTrackingService.enroll(new EnrollmentRequest(externalId, scheduleName, preferredAlertTime, referenceDate, null, null));

        verify(enrollmentService).enroll(externalId, scheduleName, firstMilestone.getName(), referenceDate, today(), preferredAlertTime);
    }

    @Test
    public void shouldEnrollEntityIntoGivenMilestoneOfTheSchedule() {
        Milestone secondMilestone = new Milestone("second_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("first_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);

        ScheduleTrackingService scheduleTrackingService = new ScheduleTrackingServiceImpl(allTrackedSchedules, allEnrollments, enrollmentService);

        String externalId = "entity_1";
        Time preferredAlertTime = new Time(8, 10);
        LocalDate referenceDate = new LocalDate(2012, 11, 2);
        scheduleTrackingService.enroll(new EnrollmentRequest(externalId, scheduleName, preferredAlertTime, referenceDate, null, secondMilestone.getName()));

        verify(enrollmentService).enroll(externalId, scheduleName, secondMilestone.getName(), referenceDate, today(), preferredAlertTime);
    }

    @Test
    public void shouldScheduleOneRepeatJobForTheSingleAlertInTheFirstMilestone() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);

        ScheduleTrackingService scheduleTrackingService = new ScheduleTrackingServiceImpl(allTrackedSchedules, allEnrollments, enrollmentService);
        String externalId = "entity_1";
        LocalDate referenceDate = new LocalDate(2012, 11, 2);
        Time preferredAlertTime = new Time(8, 10);
        scheduleTrackingService.enroll(new EnrollmentRequest(externalId, scheduleName, preferredAlertTime, referenceDate, null, null));

        verify(enrollmentService).enroll(externalId, scheduleName, milestone.getName(), referenceDate, today(), preferredAlertTime);
    }

    @Test
    public void shouldFulfillTheCurrentMilestone() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        ScheduleTrackingService scheduleTrackingService = new ScheduleTrackingServiceImpl(allTrackedSchedules, allEnrollments, enrollmentService);

        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(null);
        scheduleTrackingService.enroll(new EnrollmentRequest("entity_1", "my_schedule", new Time(8, 10), new LocalDate(2012, 11, 2), null, null));

        Enrollment enrollment = mock(Enrollment.class);
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);

        scheduleTrackingService.fulfillCurrentMilestone("entity_1", "my_schedule", null);

        verify(enrollmentService).fulfillCurrentMilestone(enrollment, null);
    }

    @Test(expected = InvalidEnrollmentException.class)
    public void shouldFailToFulfillCurrentMilestoneIfItIsNotFoundOrNotActive() {
        when(allEnrollments.getActiveEnrollment("WRONG-ID", "WRONG-NAME")).thenReturn(null);

        ScheduleTrackingService scheduleTrackingService = new ScheduleTrackingServiceImpl(allTrackedSchedules, allEnrollments, enrollmentService);

        scheduleTrackingService.fulfillCurrentMilestone("WRONG-ID", "WRONG-NAME", null);

        verifyZeroInteractions(enrollmentService);
    }

    @Test
    public void shouldUnenrollEntityFromTheSchedule() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        ScheduleTrackingService scheduleTrackingService = new ScheduleTrackingServiceImpl(allTrackedSchedules, allEnrollments, enrollmentService);

        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", weeksAgo(4), weeksAgo(4), new Time(8, 10));
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);
        scheduleTrackingService.unenroll("entity_1", "my_schedule");

        verify(enrollmentService).unenroll(enrollment);
    }

    @Test
    public void shouldSafelyUnenrollEntityFromListOfSchedule() {
        Milestone milestone1 = new Milestone("milestone1", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone1.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        Schedule schedule1 = new Schedule("my_schedule1");
        schedule1.addMilestones(milestone1);
        when(allTrackedSchedules.getByName("my_schedule1")).thenReturn(schedule1);

        Milestone milestone2 = new Milestone("milestone2", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone2.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        Schedule schedule2 = new Schedule("my_schedule2");
        schedule2.addMilestones(milestone2);
        when(allTrackedSchedules.getByName("my_schedule2")).thenReturn(schedule2);

        ScheduleTrackingService scheduleTrackingService = new ScheduleTrackingServiceImpl(allTrackedSchedules, allEnrollments, enrollmentService);

        Enrollment enrollment1 = new Enrollment("entity_1", "my_schedule1", "milestone1", weeksAgo(4), weeksAgo(4), new Time(8, 10));
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule1")).thenReturn(enrollment1);
        Enrollment enrollment2 = new Enrollment("entity_1", "my_schedule2", "milestone2", weeksAgo(4), weeksAgo(4), new Time(8, 10));
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule2")).thenReturn(enrollment2);

        scheduleTrackingService.safeUnEnroll("entity_1", Arrays.asList("my_schedule1","my_schedule2"));

        verify(enrollmentService).unenroll(enrollment1);
        verify(enrollmentService).unenroll(enrollment2);
    }

    @Test(expected = InvalidEnrollmentException.class)
    public void shouldThrowExceptionIfEntityIsNotEnrolledIntoSchedule() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        ScheduleTrackingService scheduleTrackingService = new ScheduleTrackingServiceImpl(allTrackedSchedules, allEnrollments, enrollmentService);

        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(null);
        scheduleTrackingService.unenroll("entity_1", "my_schedule");
    }

    @Test
    public void shouldReturnEnrollmentDetails() {
        String externalId = "external id";
        String scheduleName = "schedule name";
        final Enrollment enrollment = new Enrollment(externalId, scheduleName, null, null, null, null);
        when(allEnrollments.getActiveEnrollment(externalId, scheduleName)).thenReturn(enrollment);
        final EnrollmentResponse response = new ScheduleTrackingServiceImpl(null, allEnrollments, null).getEnrollment(externalId, scheduleName);
        assertThat(response.getExternalId(), is(equalTo(externalId)));
    }

    @Test(expected = InvalidEnrollmentException.class)
    public void shouldNotFulfillAnyInactiveEnrollment() {
        String externalId = "externalId";
        String scheduleName = "scheduleName";
        when(allEnrollments.getActiveEnrollment(externalId, scheduleName)).thenReturn(null);

        ScheduleTrackingService scheduleTrackingService = new ScheduleTrackingServiceImpl(allTrackedSchedules, allEnrollments, enrollmentService);
        scheduleTrackingService.fulfillCurrentMilestone(externalId, scheduleName, DateUtil.today());
    }
}
