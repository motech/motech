package org.motechproject.server.event.scheduled;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.event.EventType;
import org.motechproject.event.EventTypeRegistry;
import org.motechproject.model.MotechScheduledEvent;

public class ScheduledEventRelayTest {

    ScheduledEventListenerRegistry registry = null;
    MotechScheduledEvent scheduledEvent = null;
    List<EventType> eventTypes = null;
    TestScheduledEventListener sel = null;

    @Before
    public void setUp() throws Exception {
        // Create event type array for creating a scheduled event listener
        eventTypes = new ArrayList<EventType>();
        eventTypes.add(new TestEventType("test", "test"));

        // Add event type to event type registry
        EventTypeRegistry.getInstance().setEventType(eventTypes.get(0));

        // Create an event listener
        sel = new TestScheduledEventListener();

        // Register the event listener
        registry = ScheduledEventListenerRegistry.getInstance();
        registry.registerListener(sel, eventTypes);

        // Create the scheduled event message object
        Map<String, Object> messageParameters = new HashMap<String, Object>();
        messageParameters.put("test", "value");
        scheduledEvent = new MotechScheduledEvent("abcd123", eventTypes.get(0).getKey(), messageParameters);
    }

    @Test
    public void testRelayEvent() throws Exception {
        ScheduledEventRelay ser = new ScheduledEventRelay();
        ser.relayEvent(scheduledEvent);
        Thread.sleep(1000);
        assertTrue(sel.handledMethodCalled());
    }

    class TestEventType implements EventType {

        private String key = null;
        private String name = null;

        public TestEventType(String name, String key) {
            this.name = name;
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    class TestScheduledEventListener implements ScheduledEventListener {

        private boolean handleCalled = false;

        @Override
        public void handle(MotechScheduledEvent event) {
            handleCalled = true;
        }

        public boolean handledMethodCalled() {
            return handleCalled;
        }
    }

}
