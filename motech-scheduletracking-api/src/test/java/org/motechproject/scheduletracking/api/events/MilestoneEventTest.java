package org.motechproject.scheduletracking.api.events;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.weeks;

public class MilestoneEventTest {
    @Test
    public void shouldCreateMotechEvent() {
        String externalId = "externalId";
        String scheduleName = "scheduleName";
        MilestoneAlert milestoneAlert =  MilestoneAlert.fromMilestone(new Milestone("M1", weeks(1), weeks(1), weeks(1), weeks(1)), LocalDate.now());
        String windowName = "windowName";

        MilestoneEvent milestoneEvent = new MilestoneEvent(externalId, scheduleName, milestoneAlert, windowName, DateUtil.today());
        MotechEvent motechEvent = milestoneEvent.toMotechEvent();

        assertEquals(EventSubjects.MILESTONE_ALERT, motechEvent.getSubject());
        Map<String, Object> parameters = motechEvent.getParameters();
        assertEquals(externalId, parameters.get(EventDataKeys.EXTERNAL_ID));
        assertEquals(scheduleName, parameters.get(EventDataKeys.SCHEDULE_NAME));
        assertEquals(milestoneAlert, parameters.get(EventDataKeys.MILESTONE_NAME));
        assertEquals(windowName, parameters.get(EventDataKeys.WINDOW_NAME));
    }

    @Test
    public void shouldCreateMotechEventFromEnrollment() {
        String externalId = "externalId";
        String scheduleName = "scheduleName";
        Milestone milestone = new Milestone("M1", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        LocalDate referenceDate = LocalDate.now();
        LocalDate enrollmentDate = LocalDate.now();
        MilestoneAlert milestoneAlert =  MilestoneAlert.fromMilestone(milestone, referenceDate);

        MilestoneWindow milestoneWindow = new MilestoneWindow(WindowName.due, new WallTime(1, WallTimeUnit.Week), new WallTime(2, WallTimeUnit.Week));
        MilestoneEvent milestoneEvent = new MilestoneEvent(new Enrollment(externalId, scheduleName, milestone.getName(), referenceDate, enrollmentDate, new Time(8, 10)), milestoneAlert, milestoneWindow);
        MotechEvent motechEvent = milestoneEvent.toMotechEvent();

        assertEquals(EventSubjects.MILESTONE_ALERT, motechEvent.getSubject());
        Map<String, Object> parameters = motechEvent.getParameters();
        assertEquals(externalId, parameters.get(EventDataKeys.EXTERNAL_ID));
        assertEquals(scheduleName, parameters.get(EventDataKeys.SCHEDULE_NAME));
        assertEquals(milestoneAlert, parameters.get(EventDataKeys.MILESTONE_NAME));
        assertEquals(milestoneWindow.getName().toString(), parameters.get(EventDataKeys.WINDOW_NAME));
    }
}
