package org.motechproject.scheduletracking.api.domain;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.wallTimeOf;

public class MilestoneTest {
    private Milestone milestone;

    @Before
    public void setUp() {
        milestone = new Milestone("M1", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
    }

    @Test
    public void shouldCreateMilestoneWindows() {
        assertNotNull(milestone.getMilestoneWindow(WindowName.Waiting));
        assertNotNull(milestone.getMilestoneWindow(WindowName.Upcoming));
        assertNotNull(milestone.getMilestoneWindow(WindowName.Due));
        assertNotNull(milestone.getMilestoneWindow(WindowName.Late));
    }

    @Test
    public void shouldReturnMilestoneWindows() {
        List<MilestoneWindow> windows = milestone.getMilestoneWindows();
        assertArrayEquals(new WindowName[]{WindowName.Waiting, WindowName.Upcoming, WindowName.Due, WindowName.Late}, extract(windows, on(MilestoneWindow.class).getName()).toArray());
    }

    @Test
    public void shouldAddAlertUnderTheMilestone() {
        Alert alert1 = new Alert(null, null, 0);
        Alert alert2 = new Alert(null, null, 0);
        milestone.addAlert(WindowName.Due, alert1);
        milestone.addAlert(WindowName.Late, alert2);
        assertArrayEquals(new Alert[] { alert1, alert2 }, milestone.getAlerts().toArray());
    }
}
