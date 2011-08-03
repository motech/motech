package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduletracking.api.BaseScheduleTrackingTest;
import org.motechproject.scheduletracking.api.domain.enrolment.Enrolment;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static junit.framework.Assert.*;

public class MilestoneTest extends BaseScheduleTrackingTest {
    private Milestone milestone;

    @Before
    public void setUp() {
        Schedule schedule = new Schedule("S1", new WallTime(2, WallTimeUnit.Week));
        milestone = new Milestone("M1", schedule, new WallTime(1, WallTimeUnit.Week), new WallTime(2, WallTimeUnit.Week), new WallTime(3, WallTimeUnit.Week), null);
    }

    @Test
    public void window() {
        assertNotNull(milestone.window(WindowName.Upcoming));
    }

    @Test
    public void fallsIn() {
        assertEquals(WindowName.Upcoming, milestone.applicableWindow(enrolment(daysAgo(9))));
        assertEquals(WindowName.Upcoming, milestone.applicableWindow(enrolment(daysAgo(7))));
        assertEquals(WindowName.Upcoming, milestone.applicableWindow(enrolment(daysAgo(14))));
        assertEquals(WindowName.Due, milestone.applicableWindow(enrolment(daysAgo(16))));
        assertEquals(WindowName.Due, milestone.applicableWindow(enrolment(daysAgo(15))));
        assertEquals(WindowName.Due, milestone.applicableWindow(enrolment(daysAgo(21))));
        assertEquals(WindowName.Past, milestone.applicableWindow(enrolment(daysAgo(22))));
    }
}
