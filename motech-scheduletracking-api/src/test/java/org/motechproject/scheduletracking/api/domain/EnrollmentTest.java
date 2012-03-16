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
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.ACTIVE, null);

        assertEquals(firstMilestone.getName(), enrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldStartWithSecondMilestone() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment lateEnrollment = new Enrollment("my_entity_1", "Yellow Fever Vaccination", "Second Shot", weeksAgo(3), weeksAgo(3), null, EnrollmentStatus.ACTIVE, null);

        assertEquals(secondMilestone.getName(), lateEnrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldReNullWhenNoMilestoneIsFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.ACTIVE, null);

        assertEquals(null, enrollment.getLastFulfilledDate());
    }

    @Test
    public void shouldReturnTheDateWhenAMilestoneWasLastFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.ACTIVE, null);
        enrollment.getFulfillments().add(new MilestoneFulfillment("First Shot", weeksAgo(0)));

        assertEquals(weeksAgo(0), enrollment.getLastFulfilledDate());
    }

    @Test
    public void newEnrollmentShouldBeActive() {
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.ACTIVE, null);
        assertTrue(enrollment.isActive());
    }

    @Test
    public void shouldCopyFromTheGivenEnrollment() {
        Enrollment newEnrollment = new Enrollment("externalId", "scheduleName", "newCurrentMilestoneName", weeksAgo(2), now(), new Time(8, 10), EnrollmentStatus.ACTIVE, null);
        Enrollment originalEnrollment = new Enrollment("externalId", "scheduleName", "currentMilestoneName", weeksAgo(3), weeksAgo(2), new Time(2, 5), EnrollmentStatus.ACTIVE, null);

        Enrollment enrollment = originalEnrollment.copyFrom(newEnrollment);

        assertEquals(newEnrollment.getExternalId(), enrollment.getExternalId());
        assertEquals(newEnrollment.getScheduleName(), enrollment.getScheduleName());
        assertEquals(newEnrollment.getCurrentMilestoneName(), enrollment.getCurrentMilestoneName());
        assertEquals(newEnrollment.getReferenceDateTime(), enrollment.getReferenceDateTime());
        assertEquals(newEnrollment.getEnrollmentDateTime(), enrollment.getEnrollmentDateTime());
        assertEquals(newEnrollment.getPreferredAlertTime(), enrollment.getPreferredAlertTime());
    }

    @Test
    public void shouldReturnReferenceDateWhenCurrentMilestoneIsTheFirstMilestone() {
        String firstMilestoneName = "first milestone";
        DateTime referenceDateTime = weeksAgo(5);
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", firstMilestoneName, referenceDateTime, weeksAgo(3), new Time(8, 20), EnrollmentStatus.ACTIVE, getMockedSchedule(firstMilestoneName, false));

        assertEquals(referenceDateTime, enrollment.getCurrentMilestoneStartDate());
    }

    @Test
    public void shouldReturnEnrollmentDateWhenEnrolledIntoSecondMilestoneAndNoMilestonesFulfilled() {
        String firstMilestoneName = "First Shot";
        String secondMilestoneName = "Second Shot";
        DateTime enrollmentDateTime = weeksAgo(3);
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", secondMilestoneName, weeksAgo(5), enrollmentDateTime, null, EnrollmentStatus.ACTIVE, getMockedSchedule(firstMilestoneName, false));

        assertEquals(enrollmentDateTime, enrollment.getCurrentMilestoneStartDate());
    }

    @Test
    public void shouldReturnReferenceDateAsTheMilestoneStartDateOfTheAnyMilestoneWhenTheScheduleIsBasedOnAbsoluteWindows() {
        String firstMilestoneName = "First Milestone";
        String secondMilestoneName = "Second Milestone";
        DateTime referenceDate = weeksAgo(5);

        Enrollment enrollmentIntoFirstMilestone = new Enrollment("ID-074285", "Yellow Fever Vaccination", firstMilestoneName, referenceDate, weeksAgo(3), null, EnrollmentStatus.ACTIVE, getMockedSchedule(firstMilestoneName, true));
        Enrollment enrollmentIntoSecondMilestone = new Enrollment("ID-074285", "Yellow Fever Vaccination", secondMilestoneName, referenceDate, weeksAgo(3), null, EnrollmentStatus.ACTIVE, getMockedSchedule(firstMilestoneName, true));

        assertEquals(referenceDate, enrollmentIntoFirstMilestone.getCurrentMilestoneStartDate());
        assertEquals(referenceDate, enrollmentIntoSecondMilestone.getCurrentMilestoneStartDate());
    }

    @Test
    public void shouldFulfillCurrentMilestone() {
        Enrollment enrollment = new Enrollment("externalId", "scheduleName", "currentMilestoneName", weeksAgo(1), weeksAgo(1), new Time(8, 10), EnrollmentStatus.ACTIVE, null);

        assertEquals(0, enrollment.getFulfillments().size());
        enrollment.fulfillCurrentMilestone(DateUtil.newDateTime(2011, 6, 5, 0, 0, 0));
        assertEquals(1, enrollment.getFulfillments().size());

        MilestoneFulfillment milestoneFulfillment = enrollment.getFulfillments().get(0);
        assertEquals(newDateTime(2011, 6, 5, 0, 0, 0), milestoneFulfillment.getFulfillmentDateTime());
        assertEquals("currentMilestoneName", milestoneFulfillment.getMilestoneName());
    }
    
    private Schedule getMockedSchedule(String firstMilestoneName, boolean isBasedOnAbsoluteWindows)
    {
        Schedule schedule = mock(Schedule.class);
        Milestone milestone = mock(Milestone.class);
        when(milestone.getName()).thenReturn(firstMilestoneName);
        when(schedule.getFirstMilestone()).thenReturn(milestone);
        when(schedule.isBasedOnAbsoluteWindows()).thenReturn(isBasedOnAbsoluteWindows);
        return schedule;
    }
}
