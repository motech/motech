package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.wallTimeOf;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;

public class EnrollmentTest {

    @Test(expected = InvalidScheduleDefinition.class)
    public void shouldNotEnrollEntityIntoScheduleHavingNoMilestones() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        new Enrollment("ID-074285", schedule, weeksAgo(5), weeksAgo(3), null);
    }

    @Test
    public void shouldStartWithFirstMilestoneByDefault() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, weeksAgo(5), weeksAgo(3), null, "First Shot");

        assertEquals(firstMilestone.getName(), enrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldStartWithSecondMilestone() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment lateEnrollment = new Enrollment("my_entity_1", schedule, weeksAgo(3), weeksAgo(3), null, "Second Shot");

        assertEquals(secondMilestone.getName(), lateEnrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldMarkAMilestoneAsFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, weeksAgo(5), weeksAgo(3), null, "First Shot");

        enrollment.fulfillCurrentMilestone(secondMilestone.getName());

        assertEquals(secondMilestone.getName(), enrollment.getCurrentMilestoneName());
        assertEquals(1, enrollment.getFulfillments().size());
    }

    @Test
    public void shouldReNullWhenNoMilestoneIsFulfilledlled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, weeksAgo(5), weeksAgo(3), null);

        assertEquals(null, enrollment.getLastFulfilledDate());
    }

    @Test
    public void shouldReturnTheDateWhenAMilestoneWasLastFulfilled() {
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        schedule.addMilestones(firstMilestone, secondMilestone);
        Enrollment enrollment = new Enrollment("ID-074285", schedule, weeksAgo(5), weeksAgo(3), null);

        enrollment.fulfillCurrentMilestone("Second Shot");

        assertEquals(weeksAgo(0), enrollment.getLastFulfilledDate());
    }
}
