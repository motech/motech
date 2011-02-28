package org.motechproject.core;

import org.junit.Test;
import org.motechproject.model.MotechScheduledEvent;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    public void equalsTest() throws Exception{
        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent(uuidStr, "testEvent", null);
        MotechScheduledEvent scheduledEventSame = new MotechScheduledEvent(uuidStr, "testEvent", null);
        MotechScheduledEvent scheduledEventDifferentJobId = new MotechScheduledEvent(uuidStr2, "testEvent", null);
        MotechScheduledEvent scheduledEventDifferentEventType = new MotechScheduledEvent(uuidStr, "testEvent2", null);

        assertTrue(scheduledEvent.equals(scheduledEvent));
        assertTrue(scheduledEvent.equals(scheduledEventSame));

        assertFalse(scheduledEvent.equals(null));
        assertFalse(scheduledEvent.equals(uuidStr));
        assertFalse(scheduledEvent.equals(scheduledEventDifferentEventType));
        assertFalse(scheduledEvent.equals(scheduledEventDifferentJobId));
    }
}
