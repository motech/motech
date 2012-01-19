package org.motechproject.scheduletracking.api.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduletracking.api.BaseScheduleTrackingTest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class MilestoneTest extends BaseScheduleTrackingTest {
    private Milestone milestone;
    private Milestone anotherMilestone;

    @Before
    public void setUp() {
        milestone = new Milestone("M1", null, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        anotherMilestone = new Milestone("M1", null, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), null);
    }

    @Test
    public void window() {
        assertNotNull(milestone.getMilestoneWindow(WindowName.Upcoming));
    }

    @Test
    public void verifyTheStateOfAMilestone() {
        assertEquals(WindowName.Waiting, milestone.getApplicableWindow(daysAgo(2)));
        assertEquals(WindowName.Waiting, milestone.getApplicableWindow(daysAgo(7)));
        assertEquals(WindowName.Upcoming, milestone.getApplicableWindow(daysAgo(8)));
        assertEquals(WindowName.Upcoming, milestone.getApplicableWindow(daysAgo(9)));
        assertEquals(WindowName.Upcoming, milestone.getApplicableWindow(daysAgo(14)));
	    assertEquals(WindowName.Due, milestone.getApplicableWindow(daysAgo(15)));
	    assertEquals(WindowName.Due, milestone.getApplicableWindow(daysAgo(16)));
        assertEquals(WindowName.Due, milestone.getApplicableWindow(daysAgo(21)));
        assertEquals(WindowName.Late, milestone.getApplicableWindow(daysAgo(22)));
        assertEquals(WindowName.Late, milestone.getApplicableWindow(daysAgo(28)));
        assertEquals(WindowName.Past, milestone.getApplicableWindow(daysAgo(29)));

        assertEquals(WindowName.Due, anotherMilestone.getApplicableWindow(daysAgo(21)));
        assertEquals(WindowName.Past, anotherMilestone.getApplicableWindow(daysAgo(22)));
        assertEquals(WindowName.Past, anotherMilestone.getApplicableWindow(daysAgo(30)));
    }
}
