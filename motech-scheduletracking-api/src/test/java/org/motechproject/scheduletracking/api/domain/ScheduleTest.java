package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.wallTimeOf;
import static org.motechproject.util.DateUtil.newDate;

public class ScheduleTest {
	private Schedule schedule;
	private Milestone firstMilestone;
	private Milestone secondMilestone;

	@Before
	public void setUp() {
		secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
		firstMilestone = new Milestone("First Shot", secondMilestone, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
		schedule = new Schedule("Yellow Fever Vaccination", wallTimeOf(52), firstMilestone);
	}

	@Test
	public void shouldGetAMilestoneBasedOnName() {
		assertThat(schedule.getMilestone("First Shot"), is(equalTo(firstMilestone)));
		assertThat(schedule.getMilestone("Second Shot"), is(equalTo(secondMilestone)));
		assertThat(schedule.getMilestone("Non Existent"), is(nullValue()));
	}

	@Test
	public void shouldDeriveEndDateBasedOnStartDateAndDuration() {
		LocalDate endDate = schedule.getEndDate(newDate(2012, 1, 2));
		assertEquals(newDate(2012, 12, 31), endDate);
	}
}
