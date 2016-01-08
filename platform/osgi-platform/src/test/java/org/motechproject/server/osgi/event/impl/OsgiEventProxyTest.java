package org.motechproject.server.osgi.event.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.motechproject.server.osgi.event.impl.OsgiEventProxyImpl;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OsgiEventProxyTest {

    private static final String SUBJECT = "test-subject";

    private OsgiEventProxy osgiEventProxy;

    @Mock
    private EventAdmin eventAdmin;

    @Before
    public void setUp() {
        osgiEventProxy = new OsgiEventProxyImpl(eventAdmin);
    }

    @Test
    public void shouldProxyEventsForQueueWithoutParams() {
        osgiEventProxy.sendEvent(SUBJECT);
        verifyEvent(false, false, new HashMap());
    }

    @Test
    public void shouldProxyEventsForQueueWithParams() {
        Map<String, Object> params = buildParams();
        osgiEventProxy.sendEvent(SUBJECT, params);
        verifyEvent(false, false, params);
    }

    @Test
    public void shouldProxyEventsForTopicWithoutParams() {
        osgiEventProxy.broadcastEvent(SUBJECT, false);
        verifyEvent(true, false, new HashMap());
    }

    @Test
    public void shouldProxyEventsForTopicWithParamsAndProxyWhenReceiving() {
        Map<String, Object> params = buildParams();
        osgiEventProxy.broadcastEvent(SUBJECT, params, true);
        verifyEvent(true, true, params);
    }


    private Map<String, Object> buildParams() {
        Map<String, Object> params = new HashMap<>();

        params.put("k1", "v1");
        params.put("k2", 22);

        return params;
    }

    private void verifyEvent(boolean broadcast, boolean proxyOnReceive, Map params) {
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventAdmin).postEvent(captor.capture());
        Event event = captor.getValue();

        assertEquals(OsgiEventProxy.PROXY_EVENT_TOPIC, event.getTopic());
        assertEquals(SUBJECT, event.getProperty(OsgiEventProxy.SUBJECT_PARAM));

        assertEquals(broadcast, event.getProperty(OsgiEventProxy.BROADCAST_PARAM));
        assertEquals(proxyOnReceive, event.getProperty(OsgiEventProxy.PROXY_ON_RECEIVING_END_PARAM));

        assertEquals(params, event.getProperty(OsgiEventProxy.PARAMETERS_PARAM));
    }
}
