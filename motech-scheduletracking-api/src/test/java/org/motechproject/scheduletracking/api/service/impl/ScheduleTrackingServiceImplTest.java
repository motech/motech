package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
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
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.days;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.weeks;
import static org.motechproject.util.DateUtil.*;

public class ScheduleTrackingServiceImplTest {

    @Mock
    private AllTrackedSchedules allTrackedSchedules;
    @Mock
    private MotechSchedulerService schedulerService;
    @Mock
    private AllEnrollments allEnrollments;
    @Mock
    private EnrollmentService enrollmentService;
    @Mock
    private EnrollmentsQueryService enrollmentsQueryService;
    
    private ScheduleTrackingService scheduleTrackingService;

    @Before
    public void setUp() {
        initMocks(this);
        scheduleTrackingService = new ScheduleTrackingServiceImpl(allTrackedSchedules, allEnrollments, enrollmentService, enrollmentsQueryService);
    }

    @Test
    public void shouldEnrollEntityIntoFirstMilestoneOfSchedule() {
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        Milestone secondMilestone = new Milestone("second_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("first_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);

        String externalId = "my_entity_1";
        DateTime referenceDateTime = now().minusDays(10);
        Time preferredAlertTime = new Time(8, 10);
        scheduleTrackingService.enroll(new EnrollmentRequest(externalId, scheduleName, preferredAlertTime, referenceDateTime.toLocalDate(), null, null, null, null));

        verify(enrollmentService).enroll(externalId, scheduleName, firstMilestone.getName(), newDateTime(referenceDateTime.toLocalDate(), new Time(0, 0)), newDateTime(now().toLocalDate(), new Time(0, 0)), preferredAlertTime);
    }

    @Test
    public void shouldEnrollEntityIntoGivenMilestoneOfTheSchedule() {
        Milestone secondMilestone = new Milestone("second_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("first_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);

        String externalId = "entity_1";
        Time preferredAlertTime = new Time(8, 10);
        DateTime referenceDateTime = newDateTime(2012, 11, 2, 0, 0, 0);
        scheduleTrackingService.enroll(new EnrollmentRequest(externalId, scheduleName, preferredAlertTime, referenceDateTime.toLocalDate(), null, null, null, secondMilestone.getName()));

        verify(enrollmentService).enroll(externalId, scheduleName, secondMilestone.getName(), newDateTime(referenceDateTime.toLocalDate(), new Time(0, 0)), newDateTime(now().toLocalDate(), new Time(0, 0)), preferredAlertTime);
    }

    @Test
    public void shouldScheduleOneRepeatJobForTheSingleAlertInTheFirstMilestone() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);

        String externalId = "entity_1";
        DateTime referenceDateTime = newDateTime(2012, 11, 2, 0, 0, 0);
        Time preferredAlertTime = new Time(8, 10);
        scheduleTrackingService.enroll(new EnrollmentRequest(externalId, scheduleName, preferredAlertTime, referenceDateTime.toLocalDate(), null, null, null, null));

        verify(enrollmentService).enroll(externalId, scheduleName, milestone.getName(), newDateTime(referenceDateTime.toLocalDate(), new Time(0, 0)), newDateTime(now().toLocalDate(), new Time(0, 0)), preferredAlertTime);
    }

    @Test
    public void shouldFulfillTheCurrentMilestone() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(null);
        scheduleTrackingService.enroll(new EnrollmentRequest("entity_1", "my_schedule", new Time(8, 10), new LocalDate(2012, 11, 2), null, null, null, null));

        Enrollment enrollment = mock(Enrollment.class);
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);

        scheduleTrackingService.fulfillCurrentMilestone("entity_1", "my_schedule");

        verify(enrollmentService).fulfillCurrentMilestone(enrollment, newDateTime(now().toLocalDate(), new Time(0, 0)));
    }

    @Test
    public void shouldFulfillTheCurrentMilestoneWithTheSpecifiedDateOnlyUsingDefaultTime() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(null);
        scheduleTrackingService.enroll(new EnrollmentRequest("entity_1", "my_schedule", new Time(8, 10), new LocalDate(2012, 11, 2), null, null, null, null));

        Enrollment enrollment = mock(Enrollment.class);
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);

        DateTime fulfillmentDateTime = newDateTime(2012, 12, 10, 0, 0, 0);
        scheduleTrackingService.fulfillCurrentMilestone("entity_1", "my_schedule", fulfillmentDateTime.toLocalDate());

        verify(enrollmentService).fulfillCurrentMilestone(enrollment, fulfillmentDateTime);
    }

    @Test
    public void shouldFulfillTheCurrentMilestoneWithTheSpecifiedDateAndTime() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(schedule);

        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(null);
        scheduleTrackingService.enroll(new EnrollmentRequest("entity_1", "my_schedule", new Time(8, 10), new LocalDate(2012, 11, 2), null, null, null, null));

        Enrollment enrollment = mock(Enrollment.class);
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);

        scheduleTrackingService.fulfillCurrentMilestone("entity_1", "my_schedule", newDate(2012, 12, 10), new Time(3, 30));

        verify(enrollmentService).fulfillCurrentMilestone(enrollment, newDateTime(2012, 12, 10, 3, 30, 0));
    }

    @Test(expected = InvalidEnrollmentException.class)
    public void shouldFailToFulfillCurrentMilestoneIfItIsNotFoundOrNotActive() {
        when(allEnrollments.getActiveEnrollment("WRONG-ID", "WRONG-NAME")).thenReturn(null);

        scheduleTrackingService.fulfillCurrentMilestone("WRONG-ID", "WRONG-NAME");

        verifyZeroInteractions(enrollmentService);
    }

    @Test
    public void shouldUnenrollEntityFromTheSchedule() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);

        String externalId = "entity_1";
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", weeksAgo(4), weeksAgo(4), new Time(8, 10), EnrollmentStatus.Active);
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);
        scheduleTrackingService.unenroll(externalId, Arrays.asList(scheduleName));

        verify(enrollmentService).unenroll(enrollment);
    }

    @Test
    public void shouldSafelyUnenrollEntityFromListOfSchedule() {
        Milestone milestone1 = new Milestone("milestone1", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone1.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        String schedule1Name = "my_schedule1";
        Schedule schedule1 = new Schedule(schedule1Name);
        schedule1.addMilestones(milestone1);
        when(allTrackedSchedules.getByName(schedule1Name)).thenReturn(schedule1);

        Milestone milestone2 = new Milestone("milestone2", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone2.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        String schedule2Name = "my_schedule2";
        Schedule schedule2 = new Schedule(schedule2Name);
        schedule2.addMilestones(milestone2);
        when(allTrackedSchedules.getByName(schedule2Name)).thenReturn(schedule2);

        String externalId = "entity_1";
        Enrollment enrollment1 = new Enrollment(externalId, schedule1Name, "milestone1", weeksAgo(4), weeksAgo(4), new Time(8, 10), EnrollmentStatus.Active);
        when(allEnrollments.getActiveEnrollment(externalId, schedule1Name)).thenReturn(enrollment1);
        Enrollment enrollment2 = new Enrollment(externalId, schedule2Name, "milestone2", weeksAgo(4), weeksAgo(4), new Time(8, 10), EnrollmentStatus.Active);
        when(allEnrollments.getActiveEnrollment(externalId, schedule2Name)).thenReturn(enrollment2);

        scheduleTrackingService.unenroll(externalId, Arrays.asList(schedule1Name, schedule2Name));

        verify(enrollmentService).unenroll(enrollment1);
        verify(enrollmentService).unenroll(enrollment2);
    }

    @Test
    public void shouldNotThrowAnyExceptionIfEntityIsNotEnrolledIntoSchedule() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        String scheduleName = "scheduleName";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);

        when(allEnrollments.getActiveEnrollment("entity_1", scheduleName)).thenReturn(null);
        scheduleTrackingService.unenroll("entity_1", Arrays.asList(scheduleName));
    }

    @Test
    public void shouldReturnEnrollmentDetails() {
        String externalId = "external id";
        String scheduleName = "schedule name";
        final Enrollment enrollment = new Enrollment(externalId, scheduleName, null, null, null, null, EnrollmentStatus.Active);
        when(allEnrollments.getActiveEnrollment(externalId, scheduleName)).thenReturn(enrollment);
        final EnrollmentResponse response = scheduleTrackingService.getEnrollment(externalId, scheduleName);
        assertThat(response.getExternalId(), is(equalTo(externalId)));
    }

    @Test(expected = InvalidEnrollmentException.class)
    public void shouldNotFulfillAnyInactiveEnrollment() {
        String externalId = "externalId";
        String scheduleName = "scheduleName";
        when(allEnrollments.getActiveEnrollment(externalId, scheduleName)).thenReturn(null);

        scheduleTrackingService.fulfillCurrentMilestone(externalId, scheduleName);
    }

    @Test
    public void shouldReturnListOfExternalIdsForTheGivenQuery() {
        EnrollmentsQuery enrollmentQuery = mock(EnrollmentsQuery.class);
        List<Enrollment> enrollments = asList(new Enrollment[]{ new Enrollment("external_id_1", null, null, null, null, null, null),  new Enrollment("external_id_2", null, null, null, null, null, null)});
        when(enrollmentsQueryService.search(enrollmentQuery)).thenReturn(enrollments);

        assertEquals(asList(new String[]{"external_id_1", "external_id_2"}), scheduleTrackingService.findExternalIds(enrollmentQuery));
    }
}
