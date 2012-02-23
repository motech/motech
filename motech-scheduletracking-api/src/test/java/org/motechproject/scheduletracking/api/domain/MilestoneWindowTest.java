package org.motechproject.scheduletracking.api.domain;

import org.joda.time.Period;
import org.junit.Test;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static org.junit.Assert.*;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.*;
import static org.motechproject.util.DateUtil.today;

public class MilestoneWindowTest {

    @Test
    public void shouldHaveMultipleAlerts() {
        MilestoneWindow window = new MilestoneWindow(WindowName.earliest, new Period(0, 0, 0, 3, 0, 0, 0, 0));
        Alert alert1 = new Alert(new WallTime(0, null), null, 0, 0);
        Alert alert2 = new Alert(new WallTime(0, null), null, 0, 1);
        window.addAlerts(alert1, alert2);
        assertArrayEquals(new Alert[]{alert1, alert2}, window.getAlerts().toArray());
    }

    @Test
    public void shouldReturnEndOfWindowInDays() {
        MilestoneWindow window = new MilestoneWindow(WindowName.earliest, new Period(0, 0, 0, 2, 0, 0, 0, 0));
        assertEquals(2, window.getWindowEndInDays());
    }

    @Test
    public void shouldGetStartDateOfTheMilestoneWindow() {
        MilestoneWindow milestoneWindow = new MilestoneWindow(WindowName.earliest, wallTimeOf(1), wallTimeOf(3));
        assertEquals(today().plusWeeks(1), milestoneWindow.getStartDate(today()));
    }
}
