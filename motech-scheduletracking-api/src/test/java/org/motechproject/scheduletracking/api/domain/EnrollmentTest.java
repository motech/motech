package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.weeks;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;

public class EnrollmentTest {
    @Test
    public void shouldStartWithFirstMilestoneByDefault() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.ACTIVE, null);

        assertEquals(firstMilestone.getName(), enrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldStartWithSecondMilestone() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment lateEnrollment = new Enrollment("my_entity_1", schedule, "Second Shot", weeksAgo(3), weeksAgo(3), null, EnrollmentStatus.ACTIVE, null);

        assertEquals(secondMilestone.getName(), lateEnrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldReNullWhenNoMilestoneIsFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.ACTIVE, null);

        assertEquals(null, enrollment.getLastFulfilledDate());
    }

    @Test
    public void shouldReturnTheDateWhenAMilestoneWasLastFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.ACTIVE, null);
        enrollment.getFulfillments().add(new MilestoneFulfillment("First Shot", weeksAgo(0)));

        assertEquals(weeksAgo(0), enrollment.getLastFulfilledDate());
    }

    @Test
    public void newEnrollmentShouldBeActive() {
        Schedule schedule = new Schedule("some_schedule");
        Enrollment enrollment = new Enrollment("ID-074285", schedule, "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.ACTIVE, null);
        assertTrue(enrollment.isActive());
    }

    @Test
    public void shouldCopyFromTheGivenEnrollment() {
        Schedule schedule = new Schedule("some_schedule");
        Enrollment newEnrollment = new Enrollment("externalId", schedule, "newCurrentMilestoneName", weeksAgo(2), now(), new Time(8, 10), EnrollmentStatus.ACTIVE, null);
        Enrollment originalEnrollment = new Enrollment("externalId", schedule, "currentMilestoneName", weeksAgo(3), weeksAgo(2), new Time(2, 5), EnrollmentStatus.ACTIVE, null);

        Enrollment enrollment = originalEnrollment.copyFrom(newEnrollment);

        assertEquals(newEnrollment.getExternalId(), enrollment.getExternalId());
        assertEquals(newEnrollment.getScheduleName(), enrollment.getScheduleName());
        assertEquals(newEnrollment.getCurrentMilestoneName(), enrollment.getCurrentMilestoneName());
        assertEquals(newEnrollment.getStartOfSchedule(), enrollment.getStartOfSchedule());
        assertEquals(newEnrollment.getEnrolledOn(), enrollment.getEnrolledOn());
        assertEquals(newEnrollment.getPreferredAlertTime(), enrollment.getPreferredAlertTime());
    }

    @Test
    public void shouldReturnReferenceDateWhenCurrentMilestoneIsTheFirstMilestone() {
        String firstMilestoneName = "first milestone";
        DateTime referenceDateTime = weeksAgo(5);
        Enrollment enrollment = new Enrollment("ID-074285", getMockedSchedule(firstMilestoneName, false), firstMilestoneName, referenceDateTime, weeksAgo(3), new Time(8, 20), EnrollmentStatus.ACTIVE, null);

        assertEquals(referenceDateTime, enrollment.getReferenceForAlerts());
    }


    @Test
    public void shouldReturnEnrollmentDateWhenEnrolledIntoSecondMilestoneAndNoMilestonesFulfilled() {
        String firstMilestoneName = "First Shot";
        String secondMilestoneName = "Second Shot";
        DateTime enrollmentDateTime = weeksAgo(3);
        Enrollment enrollment = new Enrollment("ID-074285", getMockedSchedule(firstMilestoneName, false), secondMilestoneName, weeksAgo(5), enrollmentDateTime, null, EnrollmentStatus.ACTIVE, null);

        assertEquals(enrollmentDateTime, enrollment.getReferenceForAlerts());
    }

    @Test
    public void shouldReturnReferenceDateAsTheMilestoneStartDateOfTheAnyMilestoneWhenTheScheduleIsBasedOnAbsoluteWindows() {
        String firstMilestoneName = "First Milestone";
        String secondMilestoneName = "Second Milestone";
        DateTime referenceDate = weeksAgo(5);

        Enrollment enrollmentIntoFirstMilestone = new Enrollment("ID-074285", getMockedSchedule(firstMilestoneName, true), firstMilestoneName, referenceDate, weeksAgo(3), null, EnrollmentStatus.ACTIVE, null);
        Enrollment enrollmentIntoSecondMilestone = new Enrollment("ID-074285", getMockedSchedule(firstMilestoneName, true), secondMilestoneName, referenceDate, weeksAgo(3), null, EnrollmentStatus.ACTIVE, null);

        assertEquals(referenceDate, enrollmentIntoFirstMilestone.getReferenceForAlerts());
        assertEquals(referenceDate, enrollmentIntoSecondMilestone.getReferenceForAlerts());
    }

    @Test
    public void shouldReturnMilestoneStartForAbsoluteScedule() {
        Schedule schedule = new Schedule("test scedule");
        Milestone milestone1 = new Milestone("milestone 1", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone milestone2 = new Milestone("milestone 2", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone milestone3 = new Milestone("milestone 3", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(milestone1, milestone2, milestone3);
        schedule.isBasedOnAbsoluteWindows(true);

        DateTime now = now();
        Enrollment enrollment = new Enrollment("ID-074285", schedule, "milestone 3", now, now, new Time(0, 0), EnrollmentStatus.ACTIVE, null);

        assertEquals(now.plusWeeks(8), enrollment.getReferenceForAlerts());
    }

    @Test
    public void shouldFulfillCurrentMilestone() {
        Schedule schedule = new Schedule("some_schedule");
        Enrollment enrollment = new Enrollment("externalId", schedule, "currentMilestoneName", weeksAgo(1), weeksAgo(1), new Time(8, 10), EnrollmentStatus.ACTIVE, null);

        assertEquals(0, enrollment.getFulfillments().size());
        enrollment.fulfillCurrentMilestone(DateUtil.newDateTime(2011, 6, 5, 0, 0, 0));
        assertEquals(1, enrollment.getFulfillments().size());

        MilestoneFulfillment milestoneFulfillment = enrollment.getFulfillments().get(0);
        assertEquals(newDateTime(2011, 6, 5, 0, 0, 0), milestoneFulfillment.getFulfillmentDateTime());
        assertEquals("currentMilestoneName", milestoneFulfillment.getMilestoneName());
    }

    private Schedule getMockedSchedule(String firstMilestoneName, boolean isBasedOnAbsoluteWindows) {
        Schedule schedule = mock(Schedule.class);
        Milestone milestone = mock(Milestone.class);
        when(milestone.getName()).thenReturn(firstMilestoneName);
        when(schedule.getFirstMilestone()).thenReturn(milestone);
        when(schedule.isBasedOnAbsoluteWindows()).thenReturn(isBasedOnAbsoluteWindows);
        return schedule;
    }
}
