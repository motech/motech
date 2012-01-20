package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.motechproject.util.DateUtil.newDate;

public class ScheduleTest extends BaseScheduleTrackingTest {
    private Schedule schedule;

    @Before
    public void setUp() {
        schedule = createSchedule();
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
