package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;
import org.motechproject.scheduletracking.api.BaseScheduleTrackingTest;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static junit.framework.Assert.*;

public class MilestoneWindowTest extends BaseScheduleTrackingTest {
    @Test
    public void isApplicableTo() {
        MilestoneWindow milestoneWindow = new MilestoneWindow(new WallTime(6, WallTimeUnit.Day), new WallTime(10, WallTimeUnit.Day));
        assertTrue(milestoneWindow.isApplicableTo(enrollment(daysAgo(7))));
        assertFalse(milestoneWindow.isApplicableTo(enrollment(daysAgo(1))));
        assertFalse(milestoneWindow.isApplicableTo(enrollment(daysAgo(11))));
    }
}
