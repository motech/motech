package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.exception.InvalidScheduleDefinitionException;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReader;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.*;

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
        assertEquals(weeks(0), firstMilestone.getAlerts().get(0).getOffset());
        assertEquals(months(1), firstMilestone.getAlerts().get(1).getOffset());

        assertEquals(0, secondMilestone.getAlerts().size());
    }

    @Test
    public void shouldCreateEmptyWindowIfOffsetIsNotSpecified() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/schedules");
        ScheduleRecord scheduleRecord = findRecord("IPTI Schedule", jsonReader.records());
        Schedule schedule = new ScheduleFactory().build(scheduleRecord);

        List<Milestone> milestones = schedule.getMilestones();
        Milestone firstMilestone = milestones.get(0);

        assertEquals(weeks(0), firstMilestone.getWindowEnd(WindowName.max));
    }

    @Test
    public void shouldCreateEmptyWindowWithCombinationOfUnits() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/schedules");
        ScheduleRecord scheduleRecord = findRecord("IPTI Schedule", jsonReader.records());
        Schedule schedule = new ScheduleFactory().build(scheduleRecord);

        List<Milestone> milestones = schedule.getMilestones();
        Milestone firstMilestone = milestones.get(0);

        assertEquals(weeks(14).plus(days(2)), firstMilestone.getWindowEnd(WindowName.due));
        assertEquals(years(1).plus(months(2)).plus(weeks(16)), firstMilestone.getWindowEnd(WindowName.late));
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
