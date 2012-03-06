package org.motechproject.scheduletracking.api.domain;

import org.joda.time.Period;
import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReader;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;

import java.util.List;

import static org.joda.time.Period.minutes;
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
        Schedule schedule = loadSchedule("IPTI Schedule", "/schedules");

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
        Schedule schedule = loadSchedule("IPTI Schedule", "/schedules");

        List<Milestone> milestones = schedule.getMilestones();
        Milestone firstMilestone = milestones.get(0);
        Milestone secondMilestone = milestones.get(1);

        assertEquals(2, firstMilestone.getAlerts().size());
        assertEquals(0, firstMilestone.getAlerts().get(0).getIndex());
        assertEquals(1, firstMilestone.getAlerts().get(1).getIndex());
        assertEquals(weeks(0), firstMilestone.getAlerts().get(0).getOffset());
        assertEquals(weeks(1).plus(hours(4)), firstMilestone.getAlerts().get(1).getOffset());

        assertEquals(0, secondMilestone.getAlerts().size());
    }

    @Test
    public void shouldSetWindowPeriodsFromOffsetsSpecifiedInJson() {
        Schedule schedule = loadSchedule("empty", "/empty-windows");

        List<Milestone> milestones = schedule.getMilestones();
        Milestone milestone = milestones.get(0);

        assertEquals(days(0), milestone.getWindowEnd(WindowName.earliest).minus(milestone.getWindowStart(WindowName.earliest)));
        assertEquals(weeks(14), milestone.getWindowEnd(WindowName.due).minus(milestone.getWindowStart(WindowName.due)));
        assertEquals(months(4).minus(weeks(14)), milestone.getWindowEnd(WindowName.late).minus(milestone.getWindowStart(WindowName.late)));
        assertEquals(years(1).minus(months(4)), milestone.getWindowEnd(WindowName.max).minus(milestone.getWindowStart(WindowName.max)));
    }

    @Test
    public void shouldCreateEmptyWindowIfDurationIsNotSpecified() {
        Schedule schedule = loadSchedule("empty", "/empty-windows");

        List<Milestone> milestones = schedule.getMilestones();
        Milestone earliestEmpty = milestones.get(0);
        Milestone dueEmpty = milestones.get(1);
        Milestone lateEmpty = milestones.get(2);
        Milestone maxEmpty = milestones.get(3);

        assertEquals(days(0), earliestEmpty.getWindowEnd(WindowName.earliest).minus(earliestEmpty.getWindowStart(WindowName.earliest)));
        assertEquals(days(0), dueEmpty.getWindowEnd(WindowName.due).minus(dueEmpty.getWindowStart(WindowName.due)));
        assertEquals(days(0), lateEmpty.getWindowEnd(WindowName.late).minus(lateEmpty.getWindowStart(WindowName.late)));
        assertEquals(days(0), maxEmpty.getWindowEnd(WindowName.max).minus(maxEmpty.getWindowStart(WindowName.max)));
    }

    @Test
    public void shouldCreateEmptyWindowWithCombinationOfUnits() {
        Schedule schedule = loadSchedule("IPTI Schedule", "/schedules");

        List<Milestone> milestones = schedule.getMilestones();
        Milestone firstMilestone = milestones.get(0);

        assertEquals(weeks(14).plus(hours(2)), firstMilestone.getWindowEnd(WindowName.due));
        assertEquals(years(1).plus(months(2)).plus(weeks(16)), firstMilestone.getWindowEnd(WindowName.late));
    }

    @Test
    public void offsetIsZeroIfNotDefined() {
        Schedule schedule = loadSchedule("schedule-without-offset-for-alert", "/alert-without-offset");
        assertEquals(new Period(), schedule.getFirstMilestone().getMilestoneWindow(WindowName.due).getAlerts().get(0).getOffset());
    }

    @Test
    public void offsetIsZeroIfEmpty() {
        Schedule schedule = loadSchedule("schedule-with-empty-offset-for-alert", "/alert-with-empty-offset");
        assertEquals(new Period(), schedule.getFirstMilestone().getMilestoneWindow(WindowName.due).getAlerts().get(0).getOffset());
    }

    @Test
    public void shouldSupportMinutesAndHoursInAlertAndWindowTimes() {
        Schedule schedule = loadSchedule("Schedule X", "/schedules-with-hours-and-minutes");

        Milestone firstMilestone = schedule.getMilestones().get(0);

        assertEquals(minutes(1), firstMilestone.getWindowEnd(WindowName.earliest));
        assertEquals(hours(2).plus(minutes(3)), firstMilestone.getWindowEnd(WindowName.due));

        assertEquals(minutes(4), firstMilestone.getMilestoneWindow(WindowName.due).getAlerts().get(0).getOffset());
        assertEquals(minutes(1), firstMilestone.getMilestoneWindow(WindowName.due).getAlerts().get(0).getInterval());

        assertEquals(weeks(1).plus(hours(4)).plus(minutes(3)), firstMilestone.getMilestoneWindow(WindowName.late).getAlerts().get(0).getOffset());
        assertEquals(hours(1).plus(minutes(2)), firstMilestone.getMilestoneWindow(WindowName.late).getAlerts().get(0).getInterval());
    }

    private ScheduleRecord findRecord(String name, List<ScheduleRecord> records) {
        for (ScheduleRecord record : records)
            if (record.name().equals(name))
                return record;
        return null;
    }

    private Schedule loadSchedule(String scheduleName, String directoryWithSchedules) {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl(directoryWithSchedules);
        ScheduleRecord scheduleRecord = findRecord(scheduleName, jsonReader.records());
        return new ScheduleFactory().build(scheduleRecord);
    }
}
