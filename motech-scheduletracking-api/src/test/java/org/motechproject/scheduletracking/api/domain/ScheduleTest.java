package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.hours;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.weeks;
import static org.motechproject.util.DateUtil.now;

public class ScheduleTest {
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

    // TODO: might be failing randomly
    @Test
    public void shouldReturnTrueIfScheduleDurationHasAlreadyExpired() {
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(2));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);

        assertEquals(true, schedule.hasExpiredBy(now().minusWeeks(10)));
        assertEquals(false, schedule.hasExpiredBy(now().minusWeeks(8)));
    }

    @Test
    public void shouldReturnTrueIfScheduleDurationHasAlreadyExpired_TestingHourUnits() {
        Milestone firstMilestone = new Milestone("First Shot", hours(1), hours(1), hours(2), hours(1));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone);

        DateTime now = now();
        assertTrue(schedule.hasExpiredBy(now.minusHours(6)));
        assertTrue(schedule.hasExpiredBy(now.minusHours(5)));
        assertFalse(schedule.hasExpiredBy(now.minusHours(4)));
    }
}