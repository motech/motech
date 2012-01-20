package org.motechproject.scheduletracking.api.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.wallTimeOf;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;

public class EnrollmentTest {
	private Enrollment enrollment;
	private Milestone firstMilestone;
	private Milestone secondMilestone;

	@Before
	public void setUp() {
		secondMilestone = new Milestone("Second Shot", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
		firstMilestone = new Milestone("First Shot", secondMilestone, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
		Schedule schedule = new Schedule("Yellow Fever Vaccination", wallTimeOf(52), firstMilestone);
		enrollment = new Enrollment("ID-074285", weeksAgo(3), schedule);
	}

	@Test
	public void shouldSetTheFirstMilestoneAsNextMilestoneOnCreation() {
		assertThat(enrollment.getCurrentMilestone(), is(equalTo(firstMilestone)));
	}

	@Test
	public void shouldGetPendingAlerts() {
		List<Alert> alerts = enrollment.getAlerts();
		assertEquals(1, alerts.size());
	}

	@Test
	public void shouldMarkAMilestoneAsFulfilled() {
		enrollment.fulfillMilestone();
		Milestone currentMilestone = enrollment.getCurrentMilestone();

		assertThat(currentMilestone, is(equalTo(secondMilestone)));
		List<MilestoneFulfillment> fulfillments = enrollment.getFulfillments();
		assertEquals(1, fulfillments.size());
	}

	@Test
	public void shouldGetAlertsForTheNextMilestoneOnceTheCurrentMilestoneIsFulfilled() {
		enrollment.fulfillMilestone();
		Milestone currentMilestone = enrollment.getCurrentMilestone();

		assertThat(currentMilestone, is(equalTo(secondMilestone)));
		List<Alert> alerts = enrollment.getAlerts();
		assertEquals(1, alerts.size());
		assertEquals(WindowName.Waiting, alerts.get(0).getWindowName());
	}
}
