package org.motechproject.scheduletracking.api.events;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.constants.EventDataKey;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.wallTimeOf;

public class MilestoneEventTest {
    @Test
    public void shouldCreateMotechEvent() {
        String externalId = "externalId";
        String scheduleName = "scheduleName";
        MilestoneAlert milestoneAlert =  MilestoneAlert.fromMilestone(new Milestone("M1", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4)), LocalDate.now());
        String windowName = "windowName";

        MilestoneEvent milestoneEvent = new MilestoneEvent(externalId, scheduleName, milestoneAlert, windowName, DateUtil.today());
        MotechEvent motechEvent = milestoneEvent.toMotechEvent();

        assertEquals(EventSubject.MILESTONE_ALERT, motechEvent.getSubject());
        Map<String, Object> parameters = motechEvent.getParameters();
        assertEquals(externalId, parameters.get(EventDataKey.EXTERNAL_ID));
        assertEquals(scheduleName, parameters.get(EventDataKey.SCHEDULE_NAME));
        assertEquals(milestoneAlert, parameters.get(EventDataKey.MILESTONE_NAME));
        assertEquals(windowName, parameters.get(EventDataKey.WINDOW_NAME));
    }
}
