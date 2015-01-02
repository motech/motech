package org.motechproject.event.listener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.domain.BuggyListener;
import org.motechproject.event.listener.impl.EventListenerRegistry;
import org.motechproject.event.listener.impl.ServerEventRelay;
import org.motechproject.event.queue.MotechEventConfig;
import org.motechproject.event.queue.OutboundEventGateway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ServerEventRelayTest {

    public static final String MESSAGE_DESTINATION = "message-destination";

    @Mock
    private OutboundEventGateway outboundEventGateway;

    @Mock
    private MotechEventConfig motechEventConfig;

    @Mock
    private EventListener eventListener;

    private ServerEventRelay eventRelay;
    private MotechEvent motechEvent;
    private EventListenerRegistry registry;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        registry = new EventListenerRegistry();
        eventRelay = new ServerEventRelay(outboundEventGateway, registry, motechEventConfig);

        // Create the scheduled event message object
        Map<String, Object> messageParameters = new HashMap<String, Object>();
        messageParameters.put("test", "value");
        motechEvent = new MotechEvent("org.motechproject.server.someevent", messageParameters);
    }

    @Test
    public void testRelayToSingleListener() throws Exception {
        registry.registerListener(eventListener, "org.motechproject.server.someevent");
        eventRelay.relayEvent(motechEvent);
        verify(eventListener).handle(motechEvent);
    }

    @Test
    public void testSplittingRelay() throws Exception {
        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);
        String firstListener;
        String secondListener;

        // Register a single listener for an event
        when(eventListener.getIdentifier()).thenReturn("SampleEventListener");
        registry.registerListener(eventListener, "org.motechproject.server.someevent");

        EventListener fel = mock(EventListener.class);
        when(fel.getIdentifier()).thenReturn("FooEventListener");
        registry.registerListener(fel, "org.motechproject.server.someevent");

        List<String> registeredListeners = asList(eventListener.getIdentifier(), fel.getIdentifier());

        eventRelay.relayEvent(motechEvent);

        verify(outboundEventGateway, times(2)).sendEventMessage(argument.capture());
        MotechEvent event = argument.getAllValues().get(0);
        firstListener = (String) event.getParameters().get("message-destination");
        assertTrue(event.getParameters().containsKey("message-destination"));
        assertTrue(registeredListeners.contains(firstListener));
        assertEvent(createEvent(motechEvent, firstListener), event);

        event = argument.getAllValues().get(1);
        secondListener = (String) event.getParameters().get("message-destination");
        assertTrue(event.getParameters().containsKey("message-destination"));
        assertTrue(registeredListeners.contains(secondListener));
        assertEvent(createEvent(motechEvent, secondListener), event);

        assertFalse(firstListener.equals(secondListener));
    }

    private MotechEvent createEvent(MotechEvent motechEvent, String destination) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("message-destination", destination);
        params.putAll(motechEvent.getParameters());
        return new MotechEvent(motechEvent.getSubject(), params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRelayNullEvent() throws Exception {
        eventRelay.relayEvent(null);
    }

    @Test
    public void shouldSetDestinationToEventIfListenerFails() {
        when(motechEventConfig.getMessageMaxRedeliveryCount()).thenReturn(2);

        BuggyListener buggyListener = new BuggyListener(1);

        registry.registerListener(buggyListener, "TEST-FOO");

        MotechEvent event = new MotechEvent("TEST-FOO");

        assertThat(event.getParameters().containsKey(MESSAGE_DESTINATION), is(false));

        eventRelay.relayEvent(event);

        assertThat(event.getParameters().get(MESSAGE_DESTINATION).toString(), is(buggyListener.getIdentifier()));

    }

    @Test
    public void testThatOnlyListenerIdentifiedByMessageDestinationHandlesEvent() throws Exception {

        when(eventListener.getIdentifier()).thenReturn("Good");
        registry.registerListener(eventListener, "org.motechproject.server.someevent");

        EventListener badListener = mock(EventListener.class);
        when(badListener.getIdentifier()).thenReturn("Bad");
        registry.registerListener(badListener, "org.motechproject.server.someevent");

        Map<String, Object> messageParameters = new HashMap<String, Object>();
        messageParameters.put("test", "value");
        messageParameters.put("message-destination", "Good");
        MotechEvent eventForGoodListener = new MotechEvent("org.motechproject.server.someevent", messageParameters);

        eventRelay.relayEvent(eventForGoodListener);

        verify(eventListener).handle(eventForGoodListener);
        verify(badListener, never()).handle(any(MotechEvent.class));
    }

    private void assertEvent(MotechEvent expected, MotechEvent copy) {
        assertEquals(expected.getSubject(), copy.getSubject());
        assertEquals(expected.getParameters(), copy.getParameters());
    }
}
