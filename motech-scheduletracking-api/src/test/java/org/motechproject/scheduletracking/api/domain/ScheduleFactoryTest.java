package org.motechproject.scheduletracking.api.domain;

import org.joda.time.Period;
import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.exception.InvalidScheduleDefinitionException;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReader;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.joda.time.Period.minutes;
import static org.joda.time.Period.seconds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.*;

public class ScheduleFactoryTest {
    @Test
    public void shouldCreateTheScheduleWithName() {
        Schedule schedule = loadSchedule("IPTI Schedule", "/schedules");
        assertNotNull(schedule);
        assertEquals("IPTI Schedule", schedule.getName());
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
    public void shouldAddRelativeAlertsToTheWindows() {
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
    public void shouldAddAbsoluteAlertsToTheWindows() {
        Schedule schedule = loadSchedule("Absolute Schedule", "/schedules");

        List<Milestone> milestones = schedule.getMilestones();
        Milestone firstMilestone = milestones.get(0);
        Milestone secondMilestone = milestones.get(1);

        assertEquals(weeks(0), firstMilestone.getAlerts().get(0).getOffset());
        assertEquals(hours(4), firstMilestone.getAlerts().get(1).getOffset());
        assertEquals(weeks(1), secondMilestone.getAlerts().get(0).getOffset());
        assertEquals(days(1), secondMilestone.getAlerts().get(1).getOffset());
    }

    @Test
    public void shouldAddFloatingParameterToAlerts() {
        Schedule schedule = loadSchedule("Schedule with floating alert", "/schedules");

        List<Milestone> milestones = schedule.getMilestones();
        Milestone firstMilestone = milestones.get(0);

        assertTrue(firstMilestone.getAlerts().get(0).isFloating());
        assertFalse(firstMilestone.getAlerts().get(1).isFloating());
    }

    @Test(expected = InvalidScheduleDefinitionException.class)
    public void shouldThrowExceptionIfAbsoluteScheduleHasFloatingAlerts() {
        loadSchedule("Absolute Schedule with floating alert", "/invalid-schedules");
    }

    @Test
    public void shouldSetWindowPeriodsFromOffsetsSpecifiedInJson() {
        Schedule schedule = loadSchedule("empty", "/schedules");

        List<Milestone> milestones = schedule.getMilestones();
        Milestone milestone = milestones.get(0);

        assertEquals(days(0), milestone.getWindowEnd(WindowName.earliest).minus(milestone.getWindowStart(WindowName.earliest)));
        assertEquals(weeks(14), milestone.getWindowEnd(WindowName.due).minus(milestone.getWindowStart(WindowName.due)));
        assertEquals(months(4).minus(weeks(14)), milestone.getWindowEnd(WindowName.late).minus(milestone.getWindowStart(WindowName.late)));
        assertEquals(years(1).minus(months(4)), milestone.getWindowEnd(WindowName.max).minus(milestone.getWindowStart(WindowName.max)));
    }

    @Test
    public void shouldCreateAbsoluteSchedule() {
        Schedule schedule = loadSchedule("Absolute Schedule", "/schedules");

        assertTrue(schedule.isBasedOnAbsoluteWindows());

        List<Milestone> milestones = schedule.getMilestones();
        Milestone milestone1 = milestones.get(0);
        Milestone milestone2 = milestones.get(1);

        assertEquals(weeks(0), milestone1.getWindowStart(WindowName.earliest));
        assertEquals(weeks(1), milestone1.getWindowEnd(WindowName.earliest));
        assertEquals(weeks(1), milestone1.getWindowStart(WindowName.due));
        assertEquals(weeks(2), milestone1.getWindowEnd(WindowName.due));
        assertEquals(weeks(2), milestone1.getWindowStart(WindowName.late));
        assertEquals(weeks(3), milestone1.getWindowEnd(WindowName.late));
        assertEquals(weeks(3), milestone1.getWindowStart(WindowName.max));
        assertEquals(weeks(3), milestone1.getWindowEnd(WindowName.max));

        assertEquals(weeks(0), milestone2.getWindowStart(WindowName.earliest));
        assertEquals(weeks(2), milestone2.getWindowEnd(WindowName.earliest));
        assertEquals(weeks(2), milestone2.getWindowStart(WindowName.due));
        assertEquals(weeks(3), milestone2.getWindowEnd(WindowName.due));
        assertEquals(weeks(3), milestone2.getWindowStart(WindowName.late));
        assertEquals(weeks(4), milestone2.getWindowEnd(WindowName.late));
        assertEquals(weeks(4), milestone2.getWindowStart(WindowName.max));
        assertEquals(weeks(5), milestone2.getWindowEnd(WindowName.max));
    }

    @Test
    public void shouldCreateRelativeSchedule() {
        Schedule schedule = loadSchedule("Relative Schedule", "/schedules");

        assertFalse(schedule.isBasedOnAbsoluteWindows());

        List<Milestone> milestones = schedule.getMilestones();
        Milestone milestone1 = milestones.get(0);
        Milestone milestone2 = milestones.get(1);

        assertEquals(weeks(0), milestone1.getWindowStart(WindowName.earliest));
        assertEquals(weeks(1), milestone1.getWindowEnd(WindowName.earliest));
        assertEquals(weeks(1), milestone1.getWindowStart(WindowName.due));
        assertEquals(weeks(2), milestone1.getWindowEnd(WindowName.due));
        assertEquals(weeks(2), milestone1.getWindowStart(WindowName.late));
        assertEquals(weeks(3), milestone1.getWindowEnd(WindowName.late));
        assertEquals(weeks(3), milestone1.getWindowStart(WindowName.max));
        assertEquals(weeks(3), milestone1.getWindowEnd(WindowName.max));

        assertEquals(weeks(0), milestone2.getWindowStart(WindowName.earliest));
        assertEquals(weeks(1), milestone2.getWindowEnd(WindowName.earliest));
        assertEquals(weeks(1), milestone2.getWindowStart(WindowName.due));
        assertEquals(weeks(2), milestone2.getWindowEnd(WindowName.due));
        assertEquals(weeks(2), milestone2.getWindowStart(WindowName.late));
        assertEquals(weeks(3), milestone2.getWindowEnd(WindowName.late));
        assertEquals(weeks(3), milestone2.getWindowStart(WindowName.max));
        assertEquals(weeks(4), milestone2.getWindowEnd(WindowName.max));
    }


    @Test
    public void shouldCreateEmptyWindowIfDurationIsNotSpecified() {
        Schedule schedule = loadSchedule("empty", "/schedules");

        List<Milestone> milestones = schedule.getMilestones();
        Milestone earliestEmpty = milestones.get(0);
        Milestone dueEmpty = milestones.get(1);
        Milestone lateEmpty = milestones.get(2);
        Milestone maxEmpty = milestones.get(3);

        assertEquals(days(0), earliestEmpty.getWindowDuration(WindowName.earliest));
        assertEquals(days(0), dueEmpty.getWindowDuration(WindowName.due));
        assertEquals(days(0), lateEmpty.getWindowDuration(WindowName.late));
        assertEquals(days(0), maxEmpty.getWindowDuration(WindowName.max));
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
    public void shouldCreateEmptyWindowIfOffsetIsNotSpecified() {
        Schedule schedule = loadSchedule("IPTI Schedule", "/schedules");

        List<Milestone> milestones = schedule.getMilestones();
        Milestone firstMilestone = milestones.get(0);

        assertEquals(weeks(14).plus(hours(2)), firstMilestone.getWindowEnd(WindowName.due));
        assertEquals(years(1).plus(months(2)).plus(weeks(16)), firstMilestone.getWindowEnd(WindowName.late));
    }

    @Test
    public void offsetIsZeroIfNotDefined() {
        Schedule schedule = loadSchedule("schedule-without-offset-for-alert", "/schedules");
        assertEquals(new Period(), schedule.getFirstMilestone().getMilestoneWindow(WindowName.due).getAlerts().get(0).getOffset());
    }

    @Test
    public void offsetIsZeroIfEmpty() {
        Schedule schedule = loadSchedule("schedule-with-empty-offset-for-alert", "/schedules");
        assertEquals(new Period(), schedule.getFirstMilestone().getMilestoneWindow(WindowName.due).getAlerts().get(0).getOffset());
    }

    @Test
    public void shouldSupportMinutesAndHoursInAlertAndWindowTimes() {
        Schedule schedule = loadSchedule("Schedule X", "/schedules");

        Milestone firstMilestone = schedule.getMilestones().get(0);

        assertEquals(minutes(1), firstMilestone.getWindowEnd(WindowName.earliest));
        assertEquals(hours(2).plus(minutes(3)), firstMilestone.getWindowEnd(WindowName.due));

        assertEquals(minutes(4), firstMilestone.getMilestoneWindow(WindowName.due).getAlerts().get(0).getOffset());
        assertEquals(minutes(1), firstMilestone.getMilestoneWindow(WindowName.due).getAlerts().get(0).getInterval());

        assertEquals(weeks(1).plus(hours(4)).plus(minutes(3)), firstMilestone.getMilestoneWindow(WindowName.late).getAlerts().get(0).getOffset());
        assertEquals(hours(1).plus(minutes(2)), firstMilestone.getMilestoneWindow(WindowName.late).getAlerts().get(0).getInterval());
    }

    @Test
    public void shouldSupportSecondsInAlertAndWindowTimes() {
        Schedule schedule = loadSchedule("Schedule With Seconds", "/schedules");

        Milestone firstMilestone = schedule.getMilestones().get(0);

        assertEquals(seconds(1), firstMilestone.getWindowEnd(WindowName.earliest));
        assertEquals(minutes(2).plus(seconds(3)), firstMilestone.getWindowEnd(WindowName.due));

        assertEquals(seconds(1), firstMilestone.getMilestoneWindow(WindowName.due).getAlerts().get(0).getOffset());
        assertEquals(seconds(3), firstMilestone.getMilestoneWindow(WindowName.due).getAlerts().get(0).getInterval());

        assertEquals(minutes(1).plus(seconds(3)), firstMilestone.getMilestoneWindow(WindowName.late).getAlerts().get(0).getOffset());
        assertEquals(minutes(1).plus(seconds(5)), firstMilestone.getMilestoneWindow(WindowName.late).getAlerts().get(0).getInterval());
    }

    private Schedule loadSchedule(String scheduleName, String directoryWithSchedules) {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl();
        ScheduleRecord scheduleRecord = findRecord(scheduleName, jsonReader.getAllSchedules(directoryWithSchedules));
        return new ScheduleFactory().build(scheduleRecord);
    }

    private ScheduleRecord findRecord(String name, List<ScheduleRecord> records) {
        for (ScheduleRecord record : records)
            if (record.name().equals(name))
                return record;
        return null;
    }
}
