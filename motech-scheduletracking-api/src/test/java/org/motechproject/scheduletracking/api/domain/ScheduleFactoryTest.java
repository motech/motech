package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.exception.InvalidScheduleDefinitionException;
import org.motechproject.scheduletracking.api.domain.userspecified.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReader;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ScheduleFactoryTest {

    @Test
    public void shouldCreateTheSchedule() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json");
        ScheduleRecord scheduleRecord = jsonReader.records().get(0);
        Schedule schedule = new ScheduleFactory().build(scheduleRecord);

        assertNotNull(schedule);
        assertEquals(scheduleRecord.name(), schedule.getName());
    }

	@Test
	public void shouldAddMilestonesToTheSchedule() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json");
        ScheduleRecord scheduleRecord = jsonReader.records().get(0);
        Schedule schedule = new ScheduleFactory().build(scheduleRecord);

        List<Milestone> milestones = schedule.getMilestones();
        Milestone firstMilestone = milestones.get(0);
        Milestone secondMilestone = milestones.get(1);

        assertEquals(2, milestones.size());
        assertEquals("IPTI 1", firstMilestone.getName());
        assertEquals("Bar", firstMilestone.getData().get("Foo"));
        assertEquals("IPTI 2", secondMilestone.getName());
	}

	@Test
	public void shouldAddAlertsToTheWindows() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json");
        ScheduleRecord scheduleRecord = jsonReader.records().get(0);
        Schedule schedule = new ScheduleFactory().build(scheduleRecord);

        List<Milestone> milestones = schedule.getMilestones();
        Milestone firstMilestone = milestones.get(0);
        Milestone secondMilestone = milestones.get(1);

        assertEquals(2, firstMilestone.getAlerts().size());
        assertEquals(0, firstMilestone.getAlerts().get(0).getIndex());
        assertEquals(1, firstMilestone.getAlerts().get(1).getIndex());
        assertEquals(0, firstMilestone.getAlerts().get(0).getOffset().inDays());
        assertEquals(7, firstMilestone.getAlerts().get(1).getOffset().inDays());

        assertEquals(0, secondMilestone.getAlerts().size());
    }

    @Test
    public void shouldCreateEmptyWindowIfOffsetIsNotSpecified() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json");
        ScheduleRecord scheduleRecord = jsonReader.records().get(0);
        Schedule schedule = new ScheduleFactory().build(scheduleRecord);

        List<Milestone> milestones = schedule.getMilestones();
        Milestone firstMilestone = milestones.get(0);

        assertEquals(13 * 7, firstMilestone.getMilestoneWindow(WindowName.earliest).getWindowEndInDays());
        assertEquals(14 * 7, firstMilestone.getMilestoneWindow(WindowName.due).getWindowEndInDays());
        assertEquals(16 * 7, firstMilestone.getMilestoneWindow(WindowName.late).getWindowEndInDays());
        assertEquals(16 * 7, firstMilestone.getMilestoneWindow(WindowName.max).getWindowEndInDays());
    }

    @Test(expected = InvalidScheduleDefinitionException.class)
    public void shouldThrowExceptionIfAlertDoesNotHaveOffset() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/alert-without-offset.json");
        ScheduleRecord scheduleRecord = jsonReader.records().get(0);
        new ScheduleFactory().build(scheduleRecord);
    }

    @Test(expected = InvalidScheduleDefinitionException.class)
    public void shouldThrowExceptionIfAlertOffsetIsEmpty() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/alert-with-empty-offset.json");
        ScheduleRecord scheduleRecord = jsonReader.records().get(0);
        new ScheduleFactory().build(scheduleRecord);
    }
}
