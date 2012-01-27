package org.motechproject.scheduletracking.api.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.daysAgo;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.wallTimeOf;

public class MilestoneTest {
    private Milestone milestone;
    private Milestone anotherMilestone;

    @Before
    public void setUp() {
        milestone = new Milestone("M1", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        anotherMilestone = new Milestone("M1", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), null);
    }

    @Test
    public void window() {
        assertNotNull(milestone.getMilestoneWindow(WindowName.Upcoming));
    }

    @Test
    public void verifyTheStateOfAMilestone() {
        assertEquals(WindowName.Waiting, milestone.getApplicableWindow(daysAgo(2)));
        assertEquals(WindowName.Waiting, milestone.getApplicableWindow(daysAgo(6)));
        assertEquals(WindowName.Upcoming, milestone.getApplicableWindow(daysAgo(7)));
        assertEquals(WindowName.Upcoming, milestone.getApplicableWindow(daysAgo(9)));
        assertEquals(WindowName.Upcoming, milestone.getApplicableWindow(daysAgo(13)));
        assertEquals(WindowName.Due, milestone.getApplicableWindow(daysAgo(14)));
	    assertEquals(WindowName.Due, milestone.getApplicableWindow(daysAgo(16)));
        assertEquals(WindowName.Due, milestone.getApplicableWindow(daysAgo(20)));
        assertEquals(WindowName.Late, milestone.getApplicableWindow(daysAgo(21)));
        assertEquals(WindowName.Late, milestone.getApplicableWindow(daysAgo(27)));
        assertEquals(WindowName.Past, milestone.getApplicableWindow(daysAgo(28)));
        assertEquals(WindowName.Past, milestone.getApplicableWindow(daysAgo(35)));

        assertEquals(WindowName.Due, anotherMilestone.getApplicableWindow(daysAgo(19)));
        assertEquals(WindowName.Late, anotherMilestone.getApplicableWindow(daysAgo(21)));
        assertEquals(WindowName.Past, anotherMilestone.getApplicableWindow(daysAgo(22)));
        assertEquals(WindowName.Past, anotherMilestone.getApplicableWindow(daysAgo(30)));
    }
}
