package org.motechproject.scheduletracking.api.domain;

import org.joda.time.Period;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.days;

public class MilestoneWindowTest {

    @Test
    public void shouldHaveMultipleAlerts() {
        MilestoneWindow window = new MilestoneWindow(WindowName.earliest, new Period(0, 0, 0, 3, 0, 0, 0, 0));
        Alert alert1 = new Alert(days(0), days(0), 0, 0, false);
        Alert alert2 = new Alert(days(0), days(0), 0, 1, false);
        window.addAlerts(alert1, alert2);
        assertArrayEquals(new Alert[]{alert1, alert2}, window.getAlerts().toArray());
    }
}
