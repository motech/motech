package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.weeks;
import static org.motechproject.util.DateUtil.today;

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

    @Test
    public void shouldReturnTrueIfScheduleDurationHasAlreadyExpired() {
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(2));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);

        assertEquals(true, schedule.hasScheduleDurationAlreadyExpired(today().minusWeeks(10)));
        assertEquals(false, schedule.hasScheduleDurationAlreadyExpired(today().minusWeeks(8)));
    }
}