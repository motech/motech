package org.motechproject.event.listener.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.domain.BuggyListener;
import org.motechproject.event.exception.CallbackServiceNotFoundException;
import org.motechproject.event.listener.EventCallbackService;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.messaging.MotechEventConfig;
import org.motechproject.event.messaging.OutboundEventGateway;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServerEventRelayTest {

    public static final String LISTENER_IDENTIFIER = "test-identifier";
    public static final String SECONDARY_LISTENER_IDENTIFIER = "secondary-test-identifier";
    public static final String SUBJECT = "org.motechproject.server.someevent";

    private static final String TEST_SERVICE_CALLBACK = "TestServiceCallback";

    @Mock
    private OutboundEventGateway outboundEventGateway;

    @Mock
    private MotechEventConfig motechEventConfig;

    @Mock
    private EventListener eventListener;

    @Mock
    private EventListener secondaryEventListener;

    @Mock
    private EventAdmin eventAdmin;

    @Mock
    private EventListenerRegistry registry;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceReference<EventCallbackService> serviceReference;

    @Mock
    private EventCallbackService callbackService;

    private ServerEventRelay eventRelay;

    @Before
    public void setUp() throws Exception {
        eventRelay = new ServerEventRelay(outboundEventGateway, registry, motechEventConfig, eventAdmin, bundleContext);

        when(eventListener.getIdentifier()).thenReturn(LISTENER_IDENTIFIER);
        when(secondaryEventListener.getIdentifier()).thenReturn(SECONDARY_LISTENER_IDENTIFIER);
    }

    @Test
    public void testRelayToSingleListenerWithMessageDestination() throws Exception {
        MotechEvent motechEvent = createEvent(LISTENER_IDENTIFIER);
        setUpListeners(SUBJECT, eventListener);
        eventRelay.relayQueueEvent(motechEvent);
        verify(eventListener).handle(motechEvent);
    }

    @Test
    public void shouldNotifyCallbackServiceOnSuccessfulEventHandling() throws Exception {
        MotechEvent motechEvent = createEvent(LISTENER_IDENTIFIER);
        motechEvent.setCallbackName(TEST_SERVICE_CALLBACK);
        setUpListeners(SUBJECT, eventListener);

        when(bundleContext.getServiceReferences(EventCallbackService.class, null)).thenReturn(Arrays.asList(serviceReference));
        when(bundleContext.getService(serviceReference)).thenReturn(callbackService);
        when(callbackService.getName()).thenReturn(TEST_SERVICE_CALLBACK);

        eventRelay.relayQueueEvent(motechEvent);

        verify(eventListener).handle(motechEvent);
        verify(callbackService).successCallback(motechEvent);
    }

    @Test(expected = CallbackServiceNotFoundException.class)
    public void shouldThrowExceptionWhenCallbackServiceOfTheGivenNameIsNotFound() throws Exception {
        MotechEvent motechEvent = createEvent(LISTENER_IDENTIFIER);
        motechEvent.setCallbackName(TEST_SERVICE_CALLBACK);
        setUpListeners(SUBJECT, eventListener);

        eventRelay.relayQueueEvent(motechEvent);

        verify(eventListener).handle(motechEvent);
        verify(callbackService).successCallback(motechEvent);
    }

    @Test
    public void shouldNotifyCallbackServiceOnFailedEventHandling() throws Exception {
        MotechEvent motechEvent = createEvent(LISTENER_IDENTIFIER);
        motechEvent.setCallbackName(TEST_SERVICE_CALLBACK);
        setUpListeners(SUBJECT, eventListener);

        when(bundleContext.getServiceReferences(EventCallbackService.class, null)).thenReturn(Arrays.asList(serviceReference));
        when(bundleContext.getService(serviceReference)).thenReturn(callbackService);
        when(callbackService.getName()).thenReturn(TEST_SERVICE_CALLBACK);
        RuntimeException initCause = new RuntimeException();
        doThrow(new RuntimeException("Failed", initCause)).when(eventListener).handle(any(MotechEvent.class));

        eventRelay.relayQueueEvent(motechEvent);

        verify(eventListener).handle(motechEvent);
        verify(callbackService).failureCallback(motechEvent, initCause);
    }

    @Test
    public void testRelayToSingleListenerWithoutMessageDestination() throws Exception {
        MotechEvent motechEvent = createEvent();
        setUpListeners(SUBJECT, eventListener);
        eventRelay.relayQueueEvent(motechEvent);
        verify(eventListener, never()).handle(motechEvent);
    }

    @Test
    public void testSplitEvents() throws Exception {
        MotechEvent motechEvent = createEvent();
        setUpListeners(SUBJECT, eventListener, secondaryEventListener);
        eventRelay.sendEventMessage(motechEvent);

        ArgumentCaptor<MotechEvent> argumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(outboundEventGateway, times(2)).sendEventMessage(argumentCaptor.capture());
        MotechEvent capturedEvent;

        capturedEvent = argumentCaptor.getAllValues().get(0);
        assertEquals(capturedEvent.getMessageDestination(), LISTENER_IDENTIFIER);

        capturedEvent = argumentCaptor.getAllValues().get(1);
        assertEquals(capturedEvent.getMessageDestination(), SECONDARY_LISTENER_IDENTIFIER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRelayNullQueueEvent() throws Exception {
        eventRelay.relayQueueEvent(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRelayNullTopicEvent() throws Exception {
        eventRelay.relayTopicEvent(null);
    }

    @Test
    public void shouldPreserveEventDestinationIfListenerFails() {
        when(motechEventConfig.getMessageMaxRedeliveryCount()).thenReturn(2);
        BuggyListener buggyListener = new BuggyListener(1);
        setUpListeners(SUBJECT, buggyListener);
        MotechEvent event = createEvent(buggyListener.getIdentifier());

        eventRelay.relayQueueEvent(event);

        assertThat(event.getMessageDestination().toString(), is(buggyListener.getIdentifier()));
    }

    @Test
    public void testThatOnlyListenerIdentifiedByMessageDestinationHandlesEvent() throws Exception {
        setUpListeners(SUBJECT, eventListener, secondaryEventListener);
        MotechEvent motechEvent = createEvent(LISTENER_IDENTIFIER);

        eventRelay.relayQueueEvent(motechEvent);

        verify(eventListener).handle(motechEvent);
        verify(secondaryEventListener, never()).handle(any(MotechEvent.class));
    }

    @Test
    public void shouldRetryEventHandlingWhenRelyingTopicEvent() {
        final BooleanValue handled = new BooleanValue(false);
        when(motechEventConfig.getMessageMaxRedeliveryCount()).thenReturn(2);
        when(eventListener.getIdentifier()).thenReturn("retrying");
        doThrow(new RuntimeException())
                .doThrow(new RuntimeException())
                .doAnswer(new Answer<Void>() {
                    @Override
                    public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                        handled.setValue(true);
                        return null;
                    }
                })
                .when(eventListener).handle(any(MotechEvent.class));
        setUpListeners(SUBJECT, eventListener);

        eventRelay.relayTopicEvent(new MotechEvent(SUBJECT));
        verify(eventListener, times(3)).handle(any(MotechEvent.class));
        assertTrue(handled.getValue());

        verify(eventAdmin, never()).postEvent(any(Event.class));
        verify(eventAdmin, never()).sendEvent(any(Event.class));
    }

    @Test
    public void shouldStopRetryingEventHandlingAfterMaxRedeliveryCountIsHitWhenRelyingTopicEvent() {
        final BooleanValue handled = new BooleanValue(false);
        when(motechEventConfig.getMessageMaxRedeliveryCount()).thenReturn(2);
        when(eventListener.getIdentifier()).thenReturn("retrying");
        doThrow(new RuntimeException())
                .doThrow(new RuntimeException())
                .doThrow(new RuntimeException())
                .doAnswer(new Answer<Void>() {
                    @Override
                    public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                        handled.setValue(true);
                        return null;
                    }
                })
                .when(eventListener).handle(any(MotechEvent.class));
        setUpListeners(SUBJECT, eventListener);

        eventRelay.relayTopicEvent(new MotechEvent(SUBJECT));
        verify(eventListener, times(3)).handle(any(MotechEvent.class));
        assertFalse(handled.getValue());
    }

    @Test
    public void shouldProxyBroadcastEventsInOSGi() {
        Map<String, Object> params =  new HashMap<>();
        params.put("proxy-in-osgi", true);
        MotechEvent event = new MotechEvent("subject", params);

        eventRelay.relayTopicEvent(event);

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventAdmin).postEvent(captor.capture());

        assertEquals("subject", captor.getValue().getTopic());
    }

    private MotechEvent createEvent(String messageDestination) {
        MotechEvent event = createEvent();
        event.setMessageDestination(messageDestination);
        return event;
    }

    private MotechEvent createEvent() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("test", "value");
        return new MotechEvent(SUBJECT, parameters);
    }

    private void setUpListeners(String subject, EventListener... listeners) {
        when(registry.getListeners(eq(subject))).thenReturn(new LinkedHashSet<>(Arrays.asList(listeners)));
    }

    private class BooleanValue {
        private Boolean value;

        public BooleanValue(Boolean value) {
            this.value = value;
        }

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }
    }
}
