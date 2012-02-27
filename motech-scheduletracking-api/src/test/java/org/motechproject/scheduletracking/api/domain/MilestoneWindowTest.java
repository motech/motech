package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static org.junit.Assert.*;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.*;
import static org.motechproject.util.DateUtil.today;

public class MilestoneWindowTest {

    @Test
    public void shouldHaveMultipleAlerts() {
        MilestoneWindow window = new MilestoneWindow(WindowName.earliest, new WallTime(0, WallTimeUnit.Day), new WallTime(3, WallTimeUnit.Day));
        Alert alert1 = new Alert(new WallTime(0, null), null, 0, 0);
        Alert alert2 = new Alert(new WallTime(0, null), null, 0, 1);
        window.addAlerts(alert1, alert2);
        assertArrayEquals(new Alert[]{alert1, alert2}, window.getAlerts().toArray());
    }

    @Test
    public void shouldReturnFalseIfNowFallsInTheWindow() {
        MilestoneWindow window = new MilestoneWindow(WindowName.earliest, new WallTime(0, WallTimeUnit.Day), new WallTime(3, WallTimeUnit.Day));
        assertFalse(window.hasElapsed(daysAgo(2)));
    }

    @Test
    public void shouldReturnFalseIfNowIsBeforeTheStartOfTheWindow() {
        MilestoneWindow window = new MilestoneWindow(WindowName.earliest, new WallTime(0, WallTimeUnit.Day), new WallTime(3, WallTimeUnit.Day));
        assertFalse(window.hasElapsed(daysAfter(4)));
    }

    @Test
    public void shouldReturnTrueIfNowIsAfterTheEndOfTheWindow() {
        MilestoneWindow window = new MilestoneWindow(WindowName.earliest, new WallTime(0, WallTimeUnit.Day), new WallTime(3, WallTimeUnit.Day));
        assertTrue(window.hasElapsed(daysAgo(4)));
    }

    @Test
    public void shouldReturnTrueIfNowIsOnTheEndOfTheWindow() {
        MilestoneWindow window = new MilestoneWindow(WindowName.earliest, new WallTime(0, WallTimeUnit.Day), new WallTime(3, WallTimeUnit.Day));
        assertTrue(window.hasElapsed(daysAgo(3)));
    }

    @Test
    public void testMilestoneWindowInclusiveExclusiveBoundaries() {
        MilestoneWindow earliestWindow = new MilestoneWindow(WindowName.earliest, new WallTime(0, WallTimeUnit.Week), new WallTime(3, WallTimeUnit.Week));
        MilestoneWindow dueWindow = new MilestoneWindow(WindowName.due, new WallTime(3, WallTimeUnit.Week), new WallTime(3, WallTimeUnit.Week));
        MilestoneWindow lateWindow = new MilestoneWindow(WindowName.late, new WallTime(3, WallTimeUnit.Week), new WallTime(4, WallTimeUnit.Week));
        MilestoneWindow maxWindow = new MilestoneWindow(WindowName.max, new WallTime(4, WallTimeUnit.Week), new WallTime(4, WallTimeUnit.Week));

        assertFalse(earliestWindow.hasElapsed(daysAgo(0)));
        assertFalse(earliestWindow.hasElapsed(daysAgo(20)));
        assertTrue(earliestWindow.hasElapsed(daysAgo(21)));

        assertFalse(dueWindow.hasElapsed(daysAgo(20)));
        assertTrue(dueWindow.hasElapsed(daysAgo(21)));

        assertFalse(lateWindow.hasElapsed(daysAgo(21)));
        assertFalse(lateWindow.hasElapsed(daysAgo(27)));
        assertTrue(lateWindow.hasElapsed(daysAgo(28)));

        assertFalse(maxWindow.hasElapsed(daysAgo(27)));
        assertTrue(maxWindow.hasElapsed(daysAgo(28)));
    }

    @Test
    public void shouldEndOnSameDayIfEndIsNull() {
        MilestoneWindow window = new MilestoneWindow(WindowName.earliest, new WallTime(0, WallTimeUnit.Day), null);
        assertEquals(1, window.getWindowEndInDays());
    }

    @Test
    public void shouldReturnEndOfWindowInDays() {
        MilestoneWindow window = new MilestoneWindow(WindowName.earliest, new WallTime(1, WallTimeUnit.Day), new WallTime(3, WallTimeUnit.Week));
        assertEquals(21, window.getWindowEndInDays());
    }

    @Test
    public void shouldGetStartDateOfTheMilestoneWindow() {
        MilestoneWindow milestoneWindow = new MilestoneWindow(WindowName.earliest, wallTimeInWeeks(1), wallTimeInWeeks(3));
        assertEquals(today().plusWeeks(1), milestoneWindow.getStartDate(today()));
    }
}
