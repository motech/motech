package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.weeks;

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
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);

        assertEquals(false, schedule.maxMilestoneCountReached(0));
        assertEquals(false, schedule.maxMilestoneCountReached(1));
        assertEquals(true, schedule.maxMilestoneCountReached(2));
        assertEquals(true, schedule.maxMilestoneCountReached(3));
    }

    @Test
    public void shouldReturnTheMaximumNumberOfDaysInTheMilestone() {
        Milestone secondMilestone = new Milestone("Second Shot", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("First Shot", weeks(1), weeks(1), weeks(1), weeks(2));
        Schedule schedule = new Schedule("Yellow Fever Vaccination");
        schedule.addMilestones(firstMilestone, secondMilestone);

        assertEquals(weeks(9), schedule.getDuration());
    }
}