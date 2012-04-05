package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.testing.utils.BaseUnitTest;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.hours;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.weeks;
import static org.motechproject.util.DateUtil.newDateTime;

public class ScheduleTest extends BaseUnitTest {
    @Test
    public void shouldGetMilestoneByName() {
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);

        assertEquals(firstMilestone, schedule.getMilestone("First Shot"));
        assertEquals(secondMilestone, schedule.getMilestone("Second Shot"));
    }

    @Test
    public void shouldReturnNextMilestone() {
        String secondMilestoneName = "Second Shot";
        Milestone secondMilestone = new Milestone(secondMilestoneName, weeks(1), weeks(1), weeks(1), weeks(1));
        String firstMilestoneName = "First Shot";
        Milestone firstMilestone = new Milestone(firstMilestoneName, weeks(1), weeks(1), weeks(1), weeks(1));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);

        assertEquals(secondMilestoneName, schedule.getNextMilestoneName(firstMilestoneName));
        assertEquals(null, schedule.getNextMilestoneName(secondMilestoneName));
    }

    @Test
    public void shouldReturnTheScheduleDuration() {
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(2));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);

        assertEquals(weeks(9), schedule.getDuration());
    }

    @Test
    public void shouldReturnTrueIfScheduleDurationHasAlreadyExpired() {

        DateTime now = newDateTime(2012, 8, 2, 1, 3, 3);
        mockCurrentDate(now);

        String milestone1 = "First Shot";
        String milestone2 = "Second Shot";
        Milestone firstMilestone = new Milestone(milestone1, weeks(1), weeks(1), weeks(1), weeks(2));
        Milestone secondMilestone = new Milestone(milestone2, weeks(1), weeks(1), weeks(1), weeks(1));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);

        assertEquals(true, schedule.hasExpiredSince(now.minusWeeks(5).minusSeconds(1), milestone1));
        assertEquals(false, schedule.hasExpiredSince(now.minusWeeks(5), milestone1));

        assertEquals(true, schedule.hasExpiredSince(now.minusWeeks(4).minusSeconds(1), milestone2));
        assertEquals(true, schedule.hasExpiredSince(now.minusWeeks(8), milestone2));
    }

    @Test
    public void shouldReturnTrueIfScheduleDurationHasAlreadyExpired_TestingHourUnits() {
        Milestone firstMilestone = new Milestone("First Shot", hours(1), hours(1), hours(2), hours(1));
        Milestone secondMilestone = new Milestone("second milestone", hours(2), hours(2), hours(4), hours(2));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);

        DateTime now = newDateTime(2012, 8, 2, 1, 3, 3);
        mockCurrentDate(now);
        assertTrue(schedule.hasExpiredSince(now.minusHours(6), firstMilestone.getName()));
        assertTrue(schedule.hasExpiredSince(now.minusHours(5).minusMinutes(1), firstMilestone.getName()));
        assertFalse(schedule.hasExpiredSince(now.minusHours(4), firstMilestone.getName()));

        assertTrue(schedule.hasExpiredSince(now.minusHours(10).minusSeconds(1), secondMilestone.getName()));
        assertFalse(schedule.hasExpiredSince(now.minusHours(10), secondMilestone.getName()));
    }
}