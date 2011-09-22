package org.motechproject.ivr.eventlogging;


import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ivr.IVRCallEvent;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class EventDataBuilderTest {
    private IVRCallEventBuilder builder;

    @Test
    public void shouldAddCallerId() {
        builder = new IVRCallEventBuilder("session_id", "external_Id", "action", new HashMap<String, String>(), DateTime.now());
        builder.withCallerId("caller_id");
        IVRCallEvent event = (IVRCallEvent) builder.build();
        String callId = event.getData().get(EventLogConstants.CALLER_ID);
        assertEquals("caller_id", callId);
    }
}
