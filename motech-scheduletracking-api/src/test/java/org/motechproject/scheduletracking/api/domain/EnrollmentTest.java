package org.motechproject.scheduletracking.api.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class EnrollmentTest extends BaseScheduleTrackingTest {
	private Enrollment enrollment;

	@Before
	public void setUp() {
		Schedule schedule = createSchedule();
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
