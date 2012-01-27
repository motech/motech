package org.motechproject.scheduletracking.api.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.userspecified.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReader;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;
import org.motechproject.util.DateUtil;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ScheduleFactoryTest {

	private Schedule schedule;
	private ScheduleRecord scheduleRecord;

	@Before
	public void setUp() {
		TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json");
		scheduleRecord = jsonReader.records().get(0);
        schedule = new ScheduleFactory().build(scheduleRecord);
	}

    @Test
    public void shouldCreateTheSchedule() {
        assertNotNull(schedule);
        assertEquals(scheduleRecord.name(), schedule.getName());
    }

	@Test
	public void shouldAddMilestonesToTheSchedule() {
        assertEquals(2, schedule.getMilestones().size());

        List<Milestone> milestones = schedule.getMilestones();
        assertEquals("IPTI 1", milestones.get(0).getName());
        assertEquals("Bar", milestones.get(0).getData().get("Foo"));
        assertEquals("IPTI 2", milestones.get(1).getName());
	}

	@Test
	public void shouldCreateMilestoneWindows() {
		Milestone milestone = schedule.getMilestones().get(0);
		assertEquals(WindowName.Waiting, milestone.getApplicableWindow(DateUtil.today().minusWeeks(10)));
		assertEquals(WindowName.Upcoming , milestone.getApplicableWindow(DateUtil.today().minusWeeks(13)));
		assertEquals(WindowName.Due, milestone.getApplicableWindow(DateUtil.today().minusWeeks(15)));
		assertEquals(WindowName.Late, milestone.getApplicableWindow(DateUtil.today().minusWeeks(16)));
	}

	@Test
	public void shouldAddAlertsToTheWindows() {

	}
}
