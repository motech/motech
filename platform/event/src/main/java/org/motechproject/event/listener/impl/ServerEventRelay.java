package org.motechproject.event.listener.impl;

import com.google.common.collect.Iterables;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.messaging.MotechEventConfig;
import org.motechproject.event.messaging.OutboundEventGateway;
import org.motechproject.event.utils.MotechProxyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handles incoming scheduled events and relays those events to the appropriate event listeners.
 * It is also used for publishing events in the ActiveMQ.
 */
@Component("eventRelay")
public class ServerEventRelay implements EventRelay {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerEventRelay.class);

    private static final String MESSAGE_DESTINATION = "message-destination";
    private static final String BROADCAST_MESSAGE = "broadcast-message";

    private EventListenerRegistry eventListenerRegistry;
    private OutboundEventGateway outboundEventGateway;
    private MotechEventConfig motechEventConfig;

    @Autowired
    public ServerEventRelay(OutboundEventGateway outboundEventGateway, EventListenerRegistry eventListenerRegistry, MotechEventConfig motechEventConfig) {
        this.outboundEventGateway = outboundEventGateway;
        this.eventListenerRegistry = eventListenerRegistry;
        this.motechEventConfig = motechEventConfig;
    }

    // @TODO either relayQueueEvent should be made private, or this method moved out to it's own class.
    @Override
    public void sendEventMessage(MotechEvent event) {
        Set<EventListener> listeners = getEventListeners(event);

        if (!listeners.isEmpty()) {
            outboundEventGateway.sendEventMessage(event);
        }
    }

    @Override
    public void broadcastEventMessage(MotechEvent event) {
        Set<EventListener> listeners = getEventListeners(event);

        if (!listeners.isEmpty()) {
            event.getParameters().put(BROADCAST_MESSAGE, Boolean.TRUE);
            outboundEventGateway.broadcastEventMessage(event);
        }
    }

    /**
     * Relays the event that were published in the message queue to all listeners of that event.
     *
     * @param event the event being relayed
     */
    public void relayQueueEvent(MotechEvent event) {
        Set<EventListener> listeners = getEventListeners(event);

        // Is this message destine for a specific listener?
        if (event.getParameters().containsKey(MESSAGE_DESTINATION)) {

            String messageDestination = (String) event.getParameters().get(MESSAGE_DESTINATION);

            for (EventListener listener : listeners) {
                if (listener.getIdentifier().equals(messageDestination)) {
                    MotechEvent e = new MotechEvent(event.getSubject(), event.getParameters());
                    handleQueueEvent(listener, e);
                    break;
                }
            }

        } else {

            // Is there a single listener?
            if (listeners.size() > 1) {
                // We need to split the message for each listener to ensure the work units
                // are completed individually. Therefore, if a message fails it will be
                // re-distributed to another server without being lost
                splitEvent(event, listeners);
            } else {
                handleQueueEvent(Iterables.getOnlyElement(listeners), event);
            }
        }
    }

    /**
     * Relays the event that were published in the message topic to all listeners of that event.
     *
     * @param event the event being relayed
     */
    public void relayTopicEvent(MotechEvent event) {
        Set<EventListener> listeners = getEventListeners(event);
        for (EventListener listener : listeners) {
            handleTopicEvent(listener, event);
        }
    }

    private void handleQueueEvent(EventListener listener, MotechEvent event) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Object target = MotechProxyUtils.getTargetIfProxied(listener);
            Thread.currentThread().setContextClassLoader(target.getClass().getClassLoader());
            listener.handle(event);

        } catch (RuntimeException e) {
            LOGGER.error("Handling error for event with subject {}", event.getSubject(), e);

            event.getParameters().put(MotechEvent.PARAM_INVALID_MOTECH_EVENT, Boolean.TRUE);
            event.getParameters().put(MESSAGE_DESTINATION, listener.getIdentifier());

            if (event.getMessageRedeliveryCount() == motechEventConfig.getMessageMaxRedeliveryCount()) {
                event.getParameters().put(MotechEvent.PARAM_DISCARDED_MOTECH_EVENT, Boolean.TRUE);
                LOGGER.error("Discarding Motech event {}. Max retry count reached.", event);
                throw e;
            }

            event.incrementMessageRedeliveryCount();
            sendEventMessage(event);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
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
            } catch (Exception e) {
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
            parameters.put(MESSAGE_DESTINATION, listener.getIdentifier());
            enrichedEventMessage = new MotechEvent(event.getSubject(), parameters);
            outboundEventGateway.sendEventMessage(enrichedEventMessage);
        }
    }

    private Set<EventListener> getEventListeners(MotechEvent event) {
        if (eventListenerRegistry == null) {
            throw new IllegalStateException("eventListenerRegistry is null");
        }
        if (event == null) {
            throw new IllegalArgumentException("Invalid request to relay null event");
        }

        Set<EventListener> listeners = eventListenerRegistry.getListeners(event.getSubject());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + listeners.size() + " for " + event.getSubject() + " in " + eventListenerRegistry.toString());
        }
        return listeners;
    }
}
