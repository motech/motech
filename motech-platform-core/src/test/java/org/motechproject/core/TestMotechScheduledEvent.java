package org.motechproject.core;

import org.junit.Test;
import org.motechproject.model.MotechScheduledEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: 2/28/11
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestMotechScheduledEvent {
    private String uuidStr = UUID.randomUUID().toString();
    private String uuidStr2 = UUID.randomUUID().toString();

    @Test
    public void newTest() throws Exception{
        MotechScheduledEvent scheduledEvent;
        boolean exceptionThrown = false;
        try {
            scheduledEvent = new MotechScheduledEvent(null, "testEvent", null);
        }
        catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            scheduledEvent = new MotechScheduledEvent(uuidStr, null, null);
        }
        catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testGetParameters() {
        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent(uuidStr, "testEvent", null);
        Map<String, Object> params = scheduledEvent.getParameters();

        assertNotNull("Expecting param object", params);

        HashMap hashMap = new HashMap();
        hashMap.put("One", new Integer(1));

        MotechScheduledEvent nonNullParams = new MotechScheduledEvent(uuidStr, "testEvent", hashMap);
        params = nonNullParams.getParameters();

        assertTrue(params.equals(hashMap));
        assertFalse(params == hashMap);
    }

    @Test
    public void equalsTest() throws Exception{
        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent(uuidStr, "testEvent", null);
        MotechScheduledEvent scheduledEventSame = new MotechScheduledEvent(uuidStr, "testEvent", null);
        MotechScheduledEvent scheduledEventDifferentJobId = new MotechScheduledEvent(uuidStr2, "testEvent", null);
        MotechScheduledEvent scheduledEventDifferentEventType = new MotechScheduledEvent(uuidStr, "testEvent2", null);

        HashMap hashMap = new HashMap();
        hashMap.put("One", new Integer(1));

        MotechScheduledEvent nonNullParams = new MotechScheduledEvent(uuidStr, "testEvent", hashMap);
        MotechScheduledEvent nonNullParams2 = new MotechScheduledEvent(uuidStr, "testEvent", hashMap);

        assertTrue(scheduledEvent.equals(scheduledEvent));
        assertTrue(scheduledEvent.equals(scheduledEventSame));
        assertTrue(nonNullParams.equals(nonNullParams2));

        assertFalse(scheduledEvent.equals(null));
        assertFalse(scheduledEvent.equals(uuidStr));
        assertFalse(scheduledEvent.equals(scheduledEventDifferentEventType));
        assertFalse(scheduledEvent.equals(scheduledEventDifferentJobId));

        assertFalse(scheduledEvent.equals(nonNullParams));
        assertFalse(nonNullParams.equals(scheduledEvent));
    }
}
