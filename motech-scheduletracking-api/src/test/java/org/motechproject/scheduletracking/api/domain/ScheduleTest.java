package org.motechproject.scheduletracking.api.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.wallTimeOf;

public class ScheduleTest {
	private Schedule schedule;
	private Milestone firstMilestone;
	private Milestone secondMilestone;

	@Before
	public void setUp() {
		secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
		firstMilestone = new Milestone("First Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
		schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);
	}

	@Test
	public void shouldGetAMilestoneBasedOnName() {
		assertEquals(firstMilestone, schedule.getMilestone("First Shot"));
		assertEquals(secondMilestone, schedule.getMilestone("Second Shot"));
	}

}