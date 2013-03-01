package org.motechproject.scheduletracking.api.events;

import org.junit.Test;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;

import static junit.framework.Assert.assertEquals;

public class UnenrolledUserEventTest {

    @Test
    public void shouldCreateUnerollEvent() {
        String externalId = "externaId";
        String scheduleName = "scheduleName";
        UnenrolledUserEvent unenrolledUserEvent = new UnenrolledUserEvent(externalId, scheduleName);
        MotechEvent event = unenrolledUserEvent.toMotechEvent();
        assertEquals(EventSubjects.USER_UNENROLLED, event.getSubject());
        assertEquals(unenrolledUserEvent.getExternalId(), event.getParameters().get(EventDataKeys.EXTERNAL_ID));
        assertEquals(unenrolledUserEvent.getScheduleName(), event.getParameters().get(EventDataKeys.SCHEDULE_NAME));
    }
}
