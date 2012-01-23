package org.motechproject.scheduletracking.api.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.userspecified.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReader;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ScheduleFactoryTest {

	private Schedule schedule;
	private ScheduleRecord scheduleRecord;

	@Before
	public void setUp() {
		TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json");
		scheduleRecord = jsonReader.records().get(0);
		schedule = ScheduleFactory.create(scheduleRecord);
	}

	@Test
	public void records() {
		assertThat(schedule, is(notNullValue()));
		assertThat(schedule.getName(), is(equalTo(scheduleRecord.name())));

		Milestone firstMilestone = schedule.getFirstMilestone();
		assertThat(firstMilestone.getName(), is(equalTo("IPTI 1")));
		assertThat(firstMilestone.getNextMilestone().getName(), is(equalTo("IPTI 2")));
		Map<String, String> data = firstMilestone.getData();
		assertThat(data.size(), is(equalTo(1)));
		assertThat(data.get("Foo"), is(equalTo("Bar")));

		Milestone secondMilestone = firstMilestone.getNextMilestone();
		assertThat(secondMilestone.getName(), is(equalTo("IPTI 2")));
		assertThat(secondMilestone.getNextMilestone(), is(nullValue()));
	}

	@Test
	public void shouldCreateTheMilestoneWindowsCorrectly() {
		Milestone firstMilestone = schedule.getFirstMilestone();
		assertEquals(WindowName.Waiting , firstMilestone.getApplicableWindow(DateUtil.today().minusWeeks(10)));
		assertEquals(WindowName.Upcoming , firstMilestone.getApplicableWindow(DateUtil.today().minusWeeks(13)));
		assertEquals(WindowName.Due , firstMilestone.getApplicableWindow(DateUtil.today().minusWeeks(15)));
		assertEquals(WindowName.Late , firstMilestone.getApplicableWindow(DateUtil.today().minusWeeks(16)));
	}
}
