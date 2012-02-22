package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.exception.InvalidScheduleDefinitionException;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReader;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ScheduleFactoryTest {

    @Test
    public void shouldCreateTheSchedule() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/schedules");
        ScheduleRecord scheduleRecord = findRecord("IPTI Schedule", jsonReader.records());
        Schedule schedule = new ScheduleFactory().build(scheduleRecord);

        assertNotNull(schedule);
        assertEquals(scheduleRecord.name(), schedule.getName());
    }

	@Test
	public void shouldAddMilestonesToTheSchedule() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/schedules");
        ScheduleRecord scheduleRecord = findRecord("IPTI Schedule", jsonReader.records());
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
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/schedules");
        ScheduleRecord scheduleRecord = findRecord("IPTI Schedule", jsonReader.records());
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
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/schedules");
        ScheduleRecord scheduleRecord = findRecord("IPTI Schedule", jsonReader.records());
        Schedule schedule = new ScheduleFactory().build(scheduleRecord);

        List<Milestone> milestones = schedule.getMilestones();
        Milestone firstMilestone = milestones.get(0);

        assertEquals(13 * 7, firstMilestone.getWindowEndInDays(WindowName.earliest));
        assertEquals(14 * 7, firstMilestone.getWindowEndInDays(WindowName.due));
        assertEquals(16 * 7, firstMilestone.getWindowEndInDays(WindowName.late));
        assertEquals(16 * 7, firstMilestone.getWindowEndInDays(WindowName.max));
    }

    @Test(expected = InvalidScheduleDefinitionException.class)
    public void shouldThrowExceptionIfAlertDoesNotHaveOffset() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/alert-without-offset");
        ScheduleRecord scheduleRecord = findRecord("schedule-without-offset-for-alert", jsonReader.records());
        new ScheduleFactory().build(scheduleRecord);
    }

    @Test(expected = InvalidScheduleDefinitionException.class)
    public void shouldThrowExceptionIfAlertOffsetIsEmpty() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/alert-with-empty-offset");
        ScheduleRecord scheduleRecord = findRecord("schedule-with-empty-offset-for-alert", jsonReader.records());
        new ScheduleFactory().build(scheduleRecord);
    }

    private ScheduleRecord findRecord(String name, List<ScheduleRecord> records) {
        for (ScheduleRecord record : records)
            if (record.name().equals(name))
                return record;
        return null;
    }
}
