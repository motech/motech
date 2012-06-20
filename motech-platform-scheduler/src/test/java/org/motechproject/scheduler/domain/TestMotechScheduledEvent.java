package org.motechproject.scheduler.domain;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class TestMotechScheduledEvent {
    private String uuidStr = UUID.randomUUID().toString();
    private String uuidStr2 = UUID.randomUUID().toString();

    @Test(expected = IllegalArgumentException.class)
    public void newTest() throws Exception {
        MotechEvent motechEvent;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        motechEvent = new MotechEvent(null, params);
    }

    @Test
    public void testGetParameters() {
        MotechEvent motechEvent = new MotechEvent("testEvent", null);
        Map<String, Object> params = motechEvent.getParameters();

        assertNotNull("Expecting param object", params);

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("One", 1);

        MotechEvent nonNullParams = new MotechEvent("testEvent", hashMap);
        params = nonNullParams.getParameters();

        assertTrue(params.equals(hashMap));
//        assertFalse(params == hashMap);  // not wrapped collection anymore.
    }

    @Test
    public void equalsTest() throws Exception {
        MotechEvent motechEvent = new MotechEvent("testEvent", null);
        MotechEvent motechEventSame = new MotechEvent("testEvent", null);
        MotechEvent motechEventDifferentJobId = new MotechEvent("testEvent", null);
        MotechEvent scheduledEventDifferentEventType = new MotechEvent("testEvent2", null);

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("One", 1);

        MotechEvent nonNullParams = new MotechEvent("testEvent", hashMap);
        MotechEvent nonNullParams2 = new MotechEvent("testEvent", hashMap);

        assertTrue(motechEvent.equals(motechEvent));
        assertTrue(motechEvent.equals(motechEventSame));
        assertTrue(nonNullParams.equals(nonNullParams2));

        assertFalse(motechEvent.equals(null));
        assertFalse(motechEvent.equals(uuidStr));
        assertFalse(motechEvent.equals(scheduledEventDifferentEventType));

        assertFalse(motechEvent.equals(nonNullParams));
        assertFalse(nonNullParams.equals(motechEvent));
    }
}
