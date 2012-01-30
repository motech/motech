package org.motechproject.scheduletracking.api.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
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
    public void shouldCreateMilestoneWindows() {
        assertNotNull(milestone.getMilestoneWindow(WindowName.Waiting));
        assertNotNull(milestone.getMilestoneWindow(WindowName.Upcoming));
        assertNotNull(milestone.getMilestoneWindow(WindowName.Due));
        assertNotNull(milestone.getMilestoneWindow(WindowName.Late));
    }
}
