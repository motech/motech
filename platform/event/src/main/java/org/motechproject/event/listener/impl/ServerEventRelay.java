package org.motechproject.event.listener.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.exception.CallbackServiceNotFoundException;
import org.motechproject.event.listener.EventCallbackService;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.messaging.MotechEventConfig;
import org.motechproject.event.messaging.OutboundEventGateway;
import org.motechproject.event.utils.MotechProxyUtils;
import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handles incoming scheduled events and relays those events to the appropriate event listeners.
 * It is also used for publishing events in the ActiveMQ.
 */
@Component("eventRelay")
public class ServerEventRelay implements EventRelay, EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerEventRelay.class);

    private static final String PROXY_IN_OSGI = "proxy-in-osgi";

    private EventListenerRegistry eventListenerRegistry;
    private OutboundEventGateway outboundEventGateway;
    private MotechEventConfig motechEventConfig;
    private EventAdmin osgiEventAdmin;
    private BundleContext bundleContext;

    @Autowired
    public ServerEventRelay(OutboundEventGateway outboundEventGateway, EventListenerRegistry eventListenerRegistry, MotechEventConfig motechEventConfig,
                            EventAdmin osgiEventAdmin, BundleContext bundleContext) {
        this.outboundEventGateway = outboundEventGateway;
        this.eventListenerRegistry = eventListenerRegistry;
        this.motechEventConfig = motechEventConfig;
        this.osgiEventAdmin = osgiEventAdmin;
        this.bundleContext = bundleContext;
    }

    // @TODO either relayQueueEvent should be made private, or this method moved out to it's own class.
    @Override
    public void sendEventMessage(MotechEvent event) {
        verifyEventNotNull(event);
        Set<EventListener> listeners = getEventListeners(event);

        if (!listeners.isEmpty()) {
            // We need to split the message for each listener to ensure the work units
            // are completed individually. Therefore, if a message fails it will be
            // re-distributed to another server without being lost
            splitEvent(event, listeners);
        }
    }

    @Override
    public void broadcastEventMessage(MotechEvent event) {
        verifyEventNotNull(event);
        Set<EventListener> listeners = getEventListeners(event);

        // broadcast the event if there are listeners for it, or if it should get proxied as an OSGi event,
        // since we don't keep track of OSGi listeners
        if (!listeners.isEmpty() || proxyInOsgi(event)) {
            event.setBroadcast(true);
            outboundEventGateway.broadcastEventMessage(event);
        }
    }

    /**
     * Relays the event that were published in the message queue to all listeners of that event.
     *
     * @param event the event being relayed
     */
    public void relayQueueEvent(MotechEvent event) {
        verifyEventNotNull(event);
        String messageDestination = event.getMessageDestination();
        if (null != messageDestination) {
            EventListener listener = getEventListener(event, messageDestination);
            if (null != listener) {
                MotechEvent e = copyMotechEvent(event);
                handleQueueEvent(listener, e);
            } else {
                LOGGER.warn("Event listener with identifier {} not present to handle the event: {}", messageDestination, event);
            }
        } else {
            LOGGER.warn("Message destination not present in event: {}", event);
        }
    }

    /**
     * Relays the event that were published in the message topic to all listeners of that event.
     *
     * @param event the event being relayed
     */
    public void relayTopicEvent(MotechEvent event) {
        verifyEventNotNull(event);
        Set<EventListener> listeners = getEventListeners(event);
        for (EventListener listener : listeners) {
            handleTopicEvent(listener, event);
        }

        // broadcast events can be also be additionally sent as OSGi events upon being received
        if (proxyInOsgi(event)) {
            sendInOSGi(event);
        }
    }

    /**
     * Receives an OSGi event with the proxy topic. This event is then proxied as a regular Motech event.
     * This allows sending Motech events without having a dependency on Event itself -  which is used by MDS.
     * Unless necessary using the proxy mechanism should be avoided.
     *
     * @param osgiEvent the OSGi event to be proxied
     */
    @Override
    public void handleEvent(Event osgiEvent) {
        String subject = (String) osgiEvent.getProperty(OsgiEventProxy.SUBJECT_PARAM);
        Map<String, Object> parameters = (Map<String, Object>) osgiEvent.getProperty(OsgiEventProxy.PARAMETERS_PARAM);
        Boolean broadcast = (Boolean) osgiEvent.getProperty(OsgiEventProxy.BROADCAST_PARAM);
        Boolean proxyOnReceivingEnd = (Boolean) osgiEvent.getProperty(OsgiEventProxy.PROXY_ON_RECEIVING_END_PARAM);

        LOGGER.debug("Relying OSGi event - subject: {}, broadcast: {}, proxyWhenReceiving: {}",
                subject, broadcast, proxyOnReceivingEnd);

        if (parameters == null) {
            parameters = new HashMap<>();
        }

        // decide whether to send this event as an OSGi event as well, after it gets received
        // OSGi events are local to their OSGi framework (MOTECH instance)
        if (proxyOnReceivingEnd != null && proxyOnReceivingEnd) {
            parameters.put(PROXY_IN_OSGI, true);
        }

        MotechEvent motechEvent = new MotechEvent(subject, parameters);

        if (broadcast != null && broadcast) {
            broadcastEventMessage(motechEvent);
        } else {
            sendEventMessage(motechEvent);
        }
    }

    private void handleQueueEvent(EventListener listener, MotechEvent event) {
        EventCallbackService callbackService = findCallbackService(event.getCallbackName());
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            Object target = MotechProxyUtils.getTargetIfProxied(listener);
            Thread.currentThread().setContextClassLoader(target.getClass().getClassLoader());
            listener.handle(event);
            if (callbackService != null) {
                callbackService.successCallback(event);
            }
        } catch (RuntimeException e) {
            LOGGER.error("Handling error for event with subject {}", event.getSubject(), e);

            if (callbackService == null || callbackService.failureCallback(event, e.getCause())) {
                event.setInvalid(true);
                event.setMessageDestination(listener.getIdentifier());

                if (event.getMessageRedeliveryCount() == motechEventConfig.getMessageMaxRedeliveryCount()) {
                    event.setDiscarded(true);
                    LOGGER.error("Discarding Motech event {}. Max retry count reached.", event);
                    throw e;
                }

                event.incrementMessageRedeliveryCount();
                outboundEventGateway.sendEventMessage(event);
            } else {
                LOGGER.info("Event failure callback service {} has prevented redelivery of failed event with subject {}.",
                        callbackService.getName(), event.getSubject());
            }
            return;
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    private EventCallbackService findCallbackService(String callbackName) {
        if (StringUtils.isEmpty(callbackName)) {
            return null;
        }

        try {
            Collection<ServiceReference<EventCallbackService>> references = bundleContext.getServiceReferences(EventCallbackService.class, null);

            for (ServiceReference<EventCallbackService> ref : references) {
                EventCallbackService callback = bundleContext.getService(ref);
                if (callback.getName().equals(callbackName)) {
                    return callback;
                }
            }
        } catch (InvalidSyntaxException e) {
            //Should never happen
            LOGGER.error("Passed filter expression is incorrect.", e);
        }

        // If a non-null callback name has been provided, yet it cannot be found in
        // the running context, this indicates an error
        throw new CallbackServiceNotFoundException(callbackName);
    }

    private void handleTopicEvent(EventListener listener, MotechEvent event) {
        int retryCount = 0;
        int maxRetryCount = motechEventConfig.getMessageMaxRedeliveryCount();
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        do {
            try {
                Object target = MotechProxyUtils.getTargetIfProxied(listener);
                Thread.currentThread().setContextClassLoader(target.getClass().getClassLoader());
                listener.handle(event);
                break;
            } catch (RuntimeException e) {
                if (retryCount < maxRetryCount) {
                    LOGGER.warn(String.format("An exception occurred when handling topic event %s by listener %s.",
                            event.toString(), listener.getIdentifier()), e);
                } else {
                    LOGGER.error(String.format("Discarding topic event %s for listener %s. Max retry count reached.",
                            event.toString(), listener.getIdentifier()), e);
                    break;
                }
            } finally {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
        } while (retryCount++ < maxRetryCount);
    }

    /**
     * Split a given message into multiple messages with specific message destination
     * parameters. Message destinations will route the message to the specific message
     * listener.
     *
     * @param event     Event message to be split
     * @param listeners A list of listeners for this given message that will be used as message destinations
     */
    private void splitEvent(MotechEvent event, Set<EventListener> listeners) {
        MotechEvent enrichedEventMessage;
        Map<String, Object> parameters;

        for (EventListener listener : listeners) {
            parameters = new HashMap<>();
            parameters.putAll(event.getParameters());
            enrichedEventMessage = new MotechEvent(event.getSubject(), parameters, event.getCallbackName());
            enrichedEventMessage.setMetadata(event.getMetadata());
            enrichedEventMessage.setMessageDestination(listener.getIdentifier());
            outboundEventGateway.sendEventMessage(enrichedEventMessage);
        }
    }

    private EventListener getEventListener(MotechEvent event, String identifier) {
        Set<EventListener> listeners = getEventListeners(event);
        for (EventListener listener : listeners) {
            if (listener.getIdentifier().equals(identifier)) {
                return listener;
            }
        }
        return null;
    }

    private Set<EventListener> getEventListeners(MotechEvent event) {
        if (eventListenerRegistry == null) {
            throw new IllegalStateException("eventListenerRegistry is null");
        }

        Set<EventListener> listeners = eventListenerRegistry.getListeners(event.getSubject());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found {} event listeners for {} in {}", listeners.size(), event.getSubject(), eventListenerRegistry);
        }
        return listeners;
    }

    private boolean proxyInOsgi(MotechEvent event) {
        Object proxyInOsgi = event.getParameters().get(PROXY_IN_OSGI);
        return proxyInOsgi instanceof Boolean && (boolean) proxyInOsgi;
    }

    private void sendInOSGi(MotechEvent motechEvent) {
        Event osgiEvent = new Event(motechEvent.getSubject(), motechEvent.getParameters());
        osgiEventAdmin.postEvent(osgiEvent);
    }

    private MotechEvent copyMotechEvent(MotechEvent event) {
        MotechEvent copy = new MotechEvent(event.getSubject(), event.getParameters());
        copy.setId(event.getId());
        copy.setMessageRedeliveryCount(event.getMessageRedeliveryCount());
        copy.setInvalid(event.isInvalid());
        copy.setDiscarded(event.isDiscarded());
        copy.setBroadcast(event.isBroadcast());
        copy.setMessageDestination(event.getMessageDestination());
        copy.setCallbackName(event.getCallbackName());
        copy.setMetadata(event.getMetadata());
        return copy;
    }

    private void verifyEventNotNull(MotechEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Invalid request to relay null event");
        }
    }
}
