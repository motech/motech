package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.weeks;

public class ScheduleTest {
	@Test
	public void shouldGetMilestoneByName() {
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);

        assertEquals(firstMilestone, schedule.getMilestone("First Shot"));
        assertEquals(secondMilestone, schedule.getMilestone("Second Shot"));
    }

    @Test
    public void shouldReturnNextMilestone() {
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);

        assertEquals("Second Shot", schedule.getNextMilestoneName("First Shot"));
        assertEquals(null, schedule.getNextMilestoneName("Second Shot"));
    }

    @Test
    public void shouldReturnTrueIfMaximumMilestonesReached() {
        Milestone secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);

        assertEquals(false, schedule.maxMilestoneCountReached(0));
        assertEquals(false, schedule.maxMilestoneCountReached(1));
        assertEquals(true, schedule.maxMilestoneCountReached(2));
        assertEquals(true, schedule.maxMilestoneCountReached(3));
    }
}