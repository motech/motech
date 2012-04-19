package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.daysAfter;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.daysAgo;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.*;

public class MilestoneTest {
    @Test
    public void shouldCreateMilestoneWindows() {
        Milestone milestone = new Milestone("M1", weeks(1), weeks(1), weeks(1), weeks(1));
        assertNotNull(milestone.getMilestoneWindow(WindowName.earliest));
        assertNotNull(milestone.getMilestoneWindow(WindowName.due));
        assertNotNull(milestone.getMilestoneWindow(WindowName.late));
        assertNotNull(milestone.getMilestoneWindow(WindowName.max));
    }

    @Test
    public void shouldReturnMilestoneWindows() {
        Milestone milestone = new Milestone("M1", weeks(1), weeks(1), weeks(1), weeks(1));
        List<MilestoneWindow> windows = milestone.getMilestoneWindows();
        assertArrayEquals(new WindowName[]{WindowName.earliest, WindowName.due, WindowName.late, WindowName.max}, extract(windows, on(MilestoneWindow.class).getName()).toArray());
    }

    @Test
    public void shouldAddAlertUnderTheMilestone() {
        Milestone milestone = new Milestone("M1", weeks(1), weeks(1), weeks(1), weeks(1));
        Alert alert1 = new Alert(days(0), days(0), 0, 0, false);
        Alert alert2 = new Alert(days(0), days(0), 0, 1, false);
        milestone.addAlert(WindowName.late, alert1);
        milestone.addAlert(WindowName.max, alert2);
        assertArrayEquals(new Alert[]{alert1, alert2}, milestone.getAlerts().toArray());
    }

    @Test
    public void windowsStartAfterDurationOfAllPreviousWindows() {
        Milestone milestone = new Milestone("M1", weeks(1), hours(2), years(1), weeks(1));
        assertEquals(weeks(0), milestone.getWindowStart(WindowName.earliest));
        assertEquals(weeks(1), milestone.getWindowStart(WindowName.due));
        assertEquals(weeks(1).plus(hours(2)), milestone.getWindowStart(WindowName.late));
        assertEquals(weeks(1).plus(hours(2)).plus(years(1)), milestone.getWindowStart(WindowName.max));
    }

    @Test
    public void windowsEndAfterTheDurationOfAllPreviousWindows() {
        Milestone milestone = new Milestone("M1", weeks(1), weeks(2), hours(3), months(1));
        assertEquals(weeks(1), milestone.getWindowEnd(WindowName.earliest));
        assertEquals(weeks(3), milestone.getWindowEnd(WindowName.due));
        assertEquals(weeks(3).plus(hours(3)), milestone.getWindowEnd(WindowName.late));
        assertEquals(months(1).plus(weeks(3)).plus(hours(3)), milestone.getWindowEnd(WindowName.max));
    }

    @Test
    public void shouldReturnDurationForAGivenMilestoneWindow() {
        Milestone milestone = new Milestone("M1", weeks(1), hours(2), years(1), weeks(1));
        assertEquals(weeks(1), milestone.getWindowDuration(WindowName.earliest));
        assertEquals(hours(2), milestone.getWindowDuration(WindowName.due));
        assertEquals(years(1), milestone.getWindowDuration(WindowName.late));
        assertEquals(weeks(1), milestone.getWindowDuration(WindowName.max));
    }

    @Test
    public void windowPeriodCanBeComposedOfMultipleUnits() {
        Milestone milestone = new Milestone("M1", weeks(1).plus(days(3)), months(2).plus(weeks(2)), weeks(1), months(1));
        assertEquals(weeks(1).plus(days(3)), milestone.getWindowEnd(WindowName.earliest));
        assertEquals(weeks(1).plus(days(3)).plus(months(2).plus(weeks(2))), milestone.getWindowEnd(WindowName.due));
    }

    @Test
    public void shouldReturnFalseIfNowFallsInTheWindow() {
        Milestone milestone = new Milestone("M1", days(3), days(0), days(0), days(0));
        assertFalse(milestone.windowElapsed(WindowName.earliest, daysAgo(2)));
    }

    @Test
    public void shouldReturnFalseIfNowIsBeforeTheStartOfTheWindow() {
        Milestone milestone = new Milestone("M1", days(3), days(2), days(0), days(0));
        assertFalse(milestone.windowElapsed(WindowName.earliest, daysAfter(4)));
    }

    @Test
    public void shouldReturnTrueIfNowIsAfterTheEndOfTheWindow() {
        Milestone milestone = new Milestone("M1", days(3), days(2), days(0), days(0));
        assertTrue(milestone.windowElapsed(WindowName.earliest, daysAgo(4)));
    }

    @Test
    public void shouldReturnTrueIfNowIsOnTheEndOfTheWindow() {
        Milestone milestone = new Milestone("M1", days(3), days(2), days(0), days(0));
        assertTrue(milestone.windowElapsed(WindowName.earliest, daysAgo(3)));
    }

    @Test
    public void testMilestoneWindowInclusiveExclusiveBoundaries() {
        Milestone milestone = new Milestone("M1", weeks(3), weeks(0), weeks(1), weeks(0));
        assertFalse(milestone.windowElapsed(WindowName.earliest, daysAgo(0)));
        assertFalse(milestone.windowElapsed(WindowName.earliest, daysAgo(20)));
        assertTrue(milestone.windowElapsed(WindowName.earliest, daysAgo(21)));

        assertFalse(milestone.windowElapsed(WindowName.due, daysAgo(20)));
        assertTrue(milestone.windowElapsed(WindowName.due, daysAgo(21)));

        assertFalse(milestone.windowElapsed(WindowName.late, daysAgo(21)));
        assertFalse(milestone.windowElapsed(WindowName.late, daysAgo(27)));
        assertTrue(milestone.windowElapsed(WindowName.late, daysAgo(28)));

        assertFalse(milestone.windowElapsed(WindowName.max, daysAgo(27)));
        assertTrue(milestone.windowElapsed(WindowName.max, daysAgo(28)));
    }
}
