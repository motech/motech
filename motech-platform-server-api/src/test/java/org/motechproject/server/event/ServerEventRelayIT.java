package org.motechproject.server.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.gateway.StubOutboundEventGateway;
import org.motechproject.metrics.MetricsAgent;
import org.motechproject.metrics.impl.MultipleMetricsAgentImpl;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ServerEventRelayIT {
    EventListenerRegistry registry;

    ServerEventRelay eventRelay;
    OutboundEventGateway outboundEventGateway;
    MotechEvent motechEvent;

    @Before
    public void setUp() throws Exception {
        MetricsAgent metricsAgent = new MultipleMetricsAgentImpl();
        registry = new EventListenerRegistry(metricsAgent);
        outboundEventGateway = Mockito.mock(StubOutboundEventGateway.class);
        eventRelay = new ServerEventRelay(outboundEventGateway, registry, metricsAgent);

        // Create the scheduled event message object
        Map<String, Object> messageParameters = new HashMap<String, Object>();
        messageParameters.put("test", "value");
        motechEvent = new MotechEvent("org.motechproject.server.someevent", messageParameters);


    }

    @Test
    public void testRelayToSingleListener() throws Exception {
        // Register a single listener for an event
        SampleEventListener sel = mock(SampleEventListener.class);
        registry.registerListener(sel, "org.motechproject.server.someevent");

        eventRelay.relayEvent(motechEvent);

        verify(sel).handle(motechEvent);
    }

    @Test
    public void testSplittingRelay() throws Exception {
        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);
        String firstListener;
        String secondListener;

        // Register a single listener for an event
        SampleEventListener sel = mock(SampleEventListener.class);
        stub(sel.getIdentifier()).toReturn("SampleEventListener");
        registry.registerListener(sel, "org.motechproject.server.someevent");

        FooEventListener fel = mock(FooEventListener.class);
        stub(fel.getIdentifier()).toReturn("FooEventListener");
        registry.registerListener(fel, "org.motechproject.server.someevent");

        eventRelay.relayEvent(motechEvent);

        verify(outboundEventGateway, times(2)).sendEventMessage(argument.capture());
        MotechEvent event = argument.getAllValues().get(0);
        firstListener = (String) event.getParameters().get("message-destination");
        assertTrue(event.getParameters().containsKey("message-destination"));

        verify(outboundEventGateway, times(2)).sendEventMessage(argument.capture());
        event = argument.getAllValues().get(1);
        secondListener = (String) event.getParameters().get("message-destination");
        assertTrue(event.getParameters().containsKey("message-destination"));

        assertFalse(firstListener.equals(secondListener));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRelayNullEvent() throws Exception {
        eventRelay.relayEvent(null);
    }

    @Test
    public void testRelaySpecificDestinationEvent() throws Exception {
        // Register a single listener for an event
        SampleEventListener sel = mock(SampleEventListener.class);
        stub(sel.getIdentifier()).toReturn("SampleEventListener");
        registry.registerListener(sel, "org.motechproject.server.someevent");

        FooEventListener fel = mock(FooEventListener.class);
        stub(fel.getIdentifier()).toReturn("FooEventListener");
        registry.registerListener(fel, "org.motechproject.server.someevent");

        // Create my own event so I don't pollute the main one with a new param
        // This event is the same as the one created in  setUp only it is augmented like a split relayed event
        Map<String, Object> originalParameters = new HashMap<String, Object>();
        originalParameters.put("test", "value");

        Map<String, Object> messageParameters = new HashMap<String, Object>();
        messageParameters.put("original-parameters", originalParameters);
        messageParameters.put("message-destination", "FooEventListener");
        MotechEvent _motechEvent = new MotechEvent("org.motechproject.server.someevent", messageParameters);

        eventRelay.relayEvent(_motechEvent);

        verify(fel).handle(motechEvent);
        verify(sel, never()).handle(any(MotechEvent.class));
    }

    class FooEventListener implements EventListener {

        @Override
        public void handle(MotechEvent event) {
        }

        @Override
        public String getIdentifier() {
            return "FooEventListener";
        }
    }
}
