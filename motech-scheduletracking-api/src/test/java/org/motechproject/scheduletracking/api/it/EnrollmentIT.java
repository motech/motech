package org.motechproject.scheduletracking.api.it;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReader;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;
import org.motechproject.scheduletracking.api.domain.userspecified.ScheduleRecord;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class EnrollmentIT {
	private Schedule schedule;

	@Before
	public void setUp() {
		TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json");
		ScheduleRecord scheduleRecord = jsonReader.records().get(0);
		schedule = ScheduleFactory.create(scheduleRecord);
	}

	@Test
	public void shouldGetAlertsForADueMilestone() {
		List<Alert> alerts = enrollAndGetAlerts(15);

		assertEquals(1, alerts.size());
		Alert alert = alerts.get(0);
		assertThat(alert.windowName(), is(equalTo(WindowName.Due)));
		Map<String, String> data = alert.data();
		assertThat(data.get("Foo"), is(equalTo("Bar")));
	}

	@Test
	public void shouldGetAlertsForAPastMilestone() {
		List<Alert> alerts = enrollAndGetAlerts(20);

		assertEquals(1, alerts.size());
		Alert alert = alerts.get(0);
		assertThat(alert.windowName(), is(equalTo(WindowName.Past)));
		Map<String, String> data = alert.data();
		assertThat(data.get("Foo"), is(equalTo("Bar")));
	}

	@Test
	public void shouldFulfillAMilestoneAndAlertsForTheOneDueNext() {
		LocalDate enrollmentDate = LocalDate.now().minusWeeks(16);
		LocalDate firstFulfillmentDate = LocalDate.now().minusWeeks(3);
		Enrollment enrollment = new Enrollment("External ID", enrollmentDate, schedule);
		enrollment.fulfillMilestone(firstFulfillmentDate);
		assertThat(enrollment.getCurrentMilestone().getName(), is(equalTo("IPTI 2")));

		List<Alert> alerts = enrollment.getAlerts();
		assertEquals(1, alerts.size());
		Alert alert = alerts.get(0);
		assertThat(alert.windowName(), is(equalTo(WindowName.Due)));
		Map<String, String> data = alert.data();
		assertThat(data.get("doo"), is(equalTo("Bar")));
	}

	private List<Alert> enrollAndGetAlerts(int numberOfWeeksSinceEnrollment) {
		LocalDate fewWeeksAgo = LocalDate.now().minusWeeks(numberOfWeeksSinceEnrollment);
		Enrollment enrollment = new Enrollment("External ID", fewWeeksAgo, schedule);
		return enrollment.getAlerts();
	}
}
