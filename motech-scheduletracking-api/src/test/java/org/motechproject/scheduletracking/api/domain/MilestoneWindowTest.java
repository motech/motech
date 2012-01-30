package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static org.junit.Assert.assertArrayEquals;

public class MilestoneWindowTest {

    @Test
    public void shouldHaveMultipleAlerts() {
        MilestoneWindow window = new MilestoneWindow(WindowName.Waiting, new WallTime(0, WallTimeUnit.Day), new WallTime(3, WallTimeUnit.Day));
        Alert alert1 = new Alert(null, null, 0);
        Alert alert2 = new Alert(null, null, 0);
        window.addAlerts(alert1, alert2);
        assertArrayEquals(new Alert[]{ alert1, alert2}, window.getAlerts().toArray());
    }

}
