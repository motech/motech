package org.motechproject.scheduletracking.api.domain;

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
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.Active);

        assertEquals(firstMilestone.getName(), enrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldStartWithSecondMilestone() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment lateEnrollment = new Enrollment("my_entity_1", "Yellow Fever Vaccination", "Second Shot", weeksAgo(3), weeksAgo(3), null, EnrollmentStatus.Active);

        assertEquals(secondMilestone.getName(), lateEnrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldReNullWhenNoMilestoneIsFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.Active);

        assertEquals(null, enrollment.getLastFulfilledDate());
    }

    @Test
    public void shouldReturnTheDateWhenAMilestoneWasLastFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.Active);
        enrollment.getFulfillments().add(new MilestoneFulfillment("First Shot", weeksAgo(0)));

        assertEquals(weeksAgo(0), enrollment.getLastFulfilledDate());
    }

    @Test
    public void newEnrollmentShouldBeActive() {
        Enrollment enrollment = new Enrollment("ID-074285", "Yellow Fever Vaccination", "First Shot", weeksAgo(5), weeksAgo(3), null, EnrollmentStatus.Active);
        assertTrue(enrollment.isActive());
    }

    @Test
    public void shouldCopyFromTheGivenEnrollment() {
        Enrollment newEnrollment = new Enrollment("externalId", "scheduleName", "newCurrentMilestoneName", weeksAgo(2), now(), new Time(8, 10), EnrollmentStatus.Active);
        Enrollment originalEnrollment = new Enrollment("externalId", "scheduleName", "currentMilestoneName", weeksAgo(3), weeksAgo(2), new Time(2, 5), EnrollmentStatus.Active);

        Enrollment enrollment = originalEnrollment.copyFrom(newEnrollment);

        assertEquals(newEnrollment.getExternalId(), enrollment.getExternalId());
        assertEquals(newEnrollment.getScheduleName(), enrollment.getScheduleName());
        assertEquals(newEnrollment.getCurrentMilestoneName(), enrollment.getCurrentMilestoneName());
        assertEquals(newEnrollment.getReferenceDateTime(), enrollment.getReferenceDateTime());
        assertEquals(newEnrollment.getEnrollmentDateTime(), enrollment.getEnrollmentDateTime());
        assertEquals(newEnrollment.getPreferredAlertTime(), enrollment.getPreferredAlertTime());
    }

    @Test
    public void shouldFulfillCurrentMilestoneWithTheSpecifiedDate() {
        Enrollment enrollment = new Enrollment("externalId", "scheduleName", "currentMilestoneName", weeksAgo(1), weeksAgo(1), new Time(8, 10), EnrollmentStatus.Active);

        assertEquals(0, enrollment.getFulfillments().size());
        enrollment.fulfillCurrentMilestone(DateUtil.newDateTime(2011, 6, 5, 0, 0, 0));
        assertEquals(1, enrollment.getFulfillments().size());

        MilestoneFulfillment milestoneFulfillment = enrollment.getFulfillments().get(0);
        assertEquals(newDateTime(2011, 6, 5, 0, 0, 0), milestoneFulfillment.getFulfillmentDateTime());
        assertEquals("currentMilestoneName", milestoneFulfillment.getMilestoneName());
    }
}
