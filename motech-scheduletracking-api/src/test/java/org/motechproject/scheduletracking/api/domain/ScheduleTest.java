package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduletracking.api.BaseScheduleTrackingTest;

import java.util.List;

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
        String milestoneName = "First Shot";
        assertThat(schedule.getMilestone(milestoneName).getName(), is(equalTo(milestoneName)));

        milestoneName = "Second Shot";
        assertThat(schedule.getMilestone(milestoneName).getName(), is(equalTo(milestoneName)));

        milestoneName = "Non Existent";
        assertThat(schedule.getMilestone(milestoneName), is(nullValue()));
    }

    @Test
    public void shouldFulfillAParticularMilestone() {
        assertThat(schedule.getNextMilestone("First Shot"), is(equalTo("Second Shot")));
        assertThat(schedule.getNextMilestone("Second Shot"), is(nullValue()));
    }

    @Test
    public void shouldNotHaveAlertsIfNoMilestoneIsAtLeastDue() {
        Milestone milestone = new Milestone("One", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Schedule schedule = new Schedule("foo", wallTimeOf(10), milestone);

        List<Alert> alerts = schedule.alertsFor(weeksAgo(2), milestone.getName());
        assertThat(alerts.size(), is(equalTo(0)));
    }

    @Test
    public void alertsForAScheduleWithSingleMilestone() {
        Milestone milestone = new Milestone("One", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Schedule schedule = new Schedule("Schedule", wallTimeOf(10), milestone);

        List<Alert> alerts = schedule.alertsFor(weeksAgo(3), milestone.getName());
        assertThat(alerts.size(), is(equalTo(1)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Due)));
    }

    @Test
    public void alertsForAScheduleWithMultipleMilestones() {
        Milestone second = new Milestone("Second", wallTimeOf(11), wallTimeOf(12), wallTimeOf(13), wallTimeOf(14));
        Milestone first = new Milestone("First", second, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Schedule schedule = new Schedule("Schedule", wallTimeOf(52), first);

        List<Alert> alerts = schedule.alertsFor(weeksAgo(3), first.getName());
        assertThat(alerts.size(), is(equalTo(1)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Due)));

        alerts = schedule.alertsFor(weeksAgo(13), second.getName());
        assertThat(alerts.size(), is(equalTo(1)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Due)));
    }

	@Test
	public void shouldDeriveEndDateBasedOnStartDateAndDuration() {
		LocalDate endDate = schedule.getEndDate(newDate(2012, 1, 2));
		assertEquals(newDate(2012, 12, 31), endDate);
	}
}
