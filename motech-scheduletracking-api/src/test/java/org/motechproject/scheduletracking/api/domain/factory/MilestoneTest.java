package org.motechproject.scheduletracking.api.domain.factory;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static junit.framework.Assert.*;

public class MilestoneTest {
    @Test
    public void getWindow() {
        Milestone milestone = new Milestone("foo", "bar");
        milestone.addMilestoneWindow(WindowName.Upcoming, new MilestoneWindow(new WallTime(1, WallTimeUnit.Week), new WallTime(2, WallTimeUnit.Week)));
        milestone.addMilestoneWindow(WindowName.Due, new MilestoneWindow(new WallTime(2, WallTimeUnit.Week), new WallTime(3, WallTimeUnit.Week)));
        milestone.addMilestoneWindow(WindowName.Late, new MilestoneWindow(new WallTime(3, WallTimeUnit.Week), new WallTime(4, WallTimeUnit.Week)));

        assertNotNull(milestone.window(WindowName.Upcoming));
    }
}
