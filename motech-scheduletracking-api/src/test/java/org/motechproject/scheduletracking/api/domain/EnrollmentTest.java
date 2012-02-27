package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
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
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.ACTIVE);

        assertEquals(firstMilestone.getName(), enrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldStartWithSecondMilestone() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment lateEnrollment = new Enrollment("my_entity_1", "Yellow Fever Vaccination", "Second Shot", weeksAgo(3), weeksAgo(3), null, EnrollmentStatus.ACTIVE);

        assertEquals(secondMilestone.getName(), lateEnrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldReNullWhenNoMilestoneIsFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.ACTIVE);

        assertEquals(null, enrollment.getLastFulfilledDate());
    }

    @Test
    public void shouldReturnTheDateWhenAMilestoneWasLastFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.ACTIVE);
        enrollment.getFulfillments().add(new MilestoneFulfillment("First Shot", weeksAgo(0)));

        assertEquals(weeksAgo(0), enrollment.getLastFulfilledDate());
    }

    @Test
    public void newEnrollmentShouldBeActive() {
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.ACTIVE);
        assertTrue(enrollment.isActive());
    }

    @Test
    public void shouldCopyFromTheGivenEnrollment() {
        Enrollment newEnrollment = new Enrollment("externalId", "scheduleName", "newCurrentMilestoneName", weeksAgo(2), now(), new Time(8, 10), EnrollmentStatus.ACTIVE);
        Enrollment originalEnrollment = new Enrollment("externalId", "scheduleName", "currentMilestoneName", weeksAgo(3), weeksAgo(2), new Time(2, 5), EnrollmentStatus.ACTIVE);

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
        String firstMilestoneName = "First Shot";
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", firstMilestoneName, weeksAgo(5), weeksAgo(3), new Time(8, 20), EnrollmentStatus.ACTIVE);

        assertEquals(weeksAgo(5), enrollment.getCurrentMilestoneStartDate(firstMilestoneName, false));
    }

    @Test
    public void shouldReturnEnrollmentDateWhenEnrolledIntoSecondMilestoneAndNoMilestonesFulfilled() {
        String firstMilestoneName = "First Shot";
        String secondMilestoneName = "Second Shot";
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", secondMilestoneName, weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.ACTIVE);

        assertEquals(weeksAgo(3), enrollment.getCurrentMilestoneStartDate(firstMilestoneName, false));
    }

    @Test
    public void shouldReturnReferenceDateAsTheMilestoneStartDateOfTheAnyMilestoneWhenTheScheduleIsBasedOnAbsoluteWindows() {
        String firstMilestoneName = "First Milestone";
        String secondMilestoneName = "Second Milestone";
        DateTime referenceDate = weeksAgo(5);

        Enrollment enrollmentIntoFirstMilestone = new Enrollment("ID-074285", "Yellow Fever Vaccination", firstMilestoneName, referenceDate, weeksAgo(3), null, EnrollmentStatus.ACTIVE);
        Enrollment enrollmentIntoSecondMilestone = new Enrollment("ID-074285", "Yellow Fever Vaccination", secondMilestoneName, referenceDate, weeksAgo(3), null, EnrollmentStatus.ACTIVE);

        assertEquals(referenceDate, enrollmentIntoFirstMilestone.getCurrentMilestoneStartDate(firstMilestoneName, true));
        assertEquals(referenceDate, enrollmentIntoSecondMilestone.getCurrentMilestoneStartDate(firstMilestoneName, true));
    }

    @Test
    public void shouldFulfillCurrentMilestone() {
        Enrollment enrollment = new Enrollment("externalId", "scheduleName", "currentMilestoneName", weeksAgo(1), weeksAgo(1), new Time(8, 10), EnrollmentStatus.ACTIVE);

        assertEquals(0, enrollment.getFulfillments().size());
        enrollment.fulfillCurrentMilestone(DateUtil.newDateTime(2011, 6, 5, 0, 0, 0));
        assertEquals(1, enrollment.getFulfillments().size());

        MilestoneFulfillment milestoneFulfillment = enrollment.getFulfillments().get(0);
        assertEquals(newDateTime(2011, 6, 5, 0, 0, 0), milestoneFulfillment.getFulfillmentDateTime());
        assertEquals("currentMilestoneName", milestoneFulfillment.getMilestoneName());
    }
}
