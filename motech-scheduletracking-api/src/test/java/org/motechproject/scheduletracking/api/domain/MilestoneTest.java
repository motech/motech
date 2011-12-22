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
        assertNotNull(milestone.window(WindowName.Upcoming));
    }

    @Test
    public void verifyTheStateOfAMilestone() {
        assertEquals(WindowName.Waiting, milestone.applicableWindow(daysAgo(2)));
        assertEquals(WindowName.Waiting, milestone.applicableWindow(daysAgo(7)));
        assertEquals(WindowName.Upcoming, milestone.applicableWindow(daysAgo(8)));
        assertEquals(WindowName.Upcoming, milestone.applicableWindow(daysAgo(9)));
        assertEquals(WindowName.Upcoming, milestone.applicableWindow(daysAgo(14)));
	    assertEquals(WindowName.Due, milestone.applicableWindow(daysAgo(15)));
	    assertEquals(WindowName.Due, milestone.applicableWindow(daysAgo(16)));
        assertEquals(WindowName.Due, milestone.applicableWindow(daysAgo(21)));
        assertEquals(WindowName.Late, milestone.applicableWindow(daysAgo(22)));
        assertEquals(WindowName.Late, milestone.applicableWindow(daysAgo(28)));
        assertEquals(WindowName.Past, milestone.applicableWindow(daysAgo(29)));

        assertEquals(WindowName.Due, anotherMilestone.applicableWindow(daysAgo(21)));
        assertEquals(WindowName.Past, anotherMilestone.applicableWindow(daysAgo(22)));
        assertEquals(WindowName.Past, anotherMilestone.applicableWindow(daysAgo(30)));
    }
}
