package org.motechproject.server.osgi.event.impl;

import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * The implementation of {@link OsgiEventProxy}. Uses the {@link EventAdmin}
 * to send OSGi events.
 */
public class OsgiEventProxyImpl implements OsgiEventProxy {

    private EventAdmin eventAdmin;

    public OsgiEventProxyImpl(EventAdmin eventAdmin) {
        this.eventAdmin = eventAdmin;
    }

    @Override
    public void sendEvent(String subject) {
        sendEvent(subject, new HashMap<String, Object>());
    }

    @Override
    public void sendEvent(String subject, Map<String, Object> parameters) {
        Event event = buildEvent(subject, parameters, false, false);
        eventAdmin.postEvent(event);
    }

    @Override
    public void broadcastEvent(String subject, boolean proxyHandledEventInOSGi) {
        broadcastEvent(subject, new HashMap<String, Object>(), proxyHandledEventInOSGi);
    }

    @Override
    public void broadcastEvent(String subject, Map<String, Object> parameters, boolean proxyOnReceivingEnd) {
        Event event = buildEvent(subject, parameters, proxyOnReceivingEnd, true);
        eventAdmin.postEvent(event);
    }

    private Event buildEvent(String subject, Map<String, Object> parameters, boolean proxyOnReceivingEnd, boolean broadcast) {
        Map<String, Object> properties = new HashMap<>();

        // the paylod will be interpreted by the event module
        properties.put(SUBJECT_PARAM, subject);
        properties.put(PARAMETERS_PARAM, parameters);
        properties.put(BROADCAST_PARAM, broadcast);
        properties.put(PROXY_ON_RECEIVING_END_PARAM, proxyOnReceivingEnd);

        return new Event(PROXY_EVENT_TOPIC, properties);
    }
}
