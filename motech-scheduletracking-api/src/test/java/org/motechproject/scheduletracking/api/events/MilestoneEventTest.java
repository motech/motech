package org.motechproject.scheduletracking.api.events;

import org.junit.Test;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerServiceImpl;
import org.motechproject.scheduletracking.api.events.constants.EventDataKey;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MilestoneEventTest {
    @Test
    public void shouldCreateMotechEvent() {
        String externalId = "externalId";
        String scheduleName = "scheduleName";
        String milestoneName = "milestoneName";
        String windowName = "windowName";

        MilestoneEvent milestoneEvent = new MilestoneEvent(externalId, scheduleName, milestoneName, windowName, DateUtil.today());
        MotechEvent motechEvent = milestoneEvent.toMotechEvent();

        assertEquals(EventSubject.MILESTONE_ALERT, motechEvent.getSubject());
        Map<String, Object> parameters = motechEvent.getParameters();
        assertEquals(externalId, parameters.get(EventDataKey.EXTERNAL_ID));
        assertEquals(scheduleName, parameters.get(EventDataKey.SCHEDULE_NAME));
        assertEquals(milestoneName, parameters.get(EventDataKey.MILESTONE_NAME));
        assertEquals(windowName, parameters.get(EventDataKey.WINDOW_NAME));
    }
}
