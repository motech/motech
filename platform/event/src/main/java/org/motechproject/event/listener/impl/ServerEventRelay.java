package org.motechproject.event.listener.impl;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.MotechEventConfig;
import org.motechproject.event.OutboundEventGateway;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.osgi.MetricsServiceManager;
import org.motechproject.event.utils.MotechProxyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class handled incoming scheduled events and relays those events to the appropriate event listeners
 */
@Component("eventRelay")
public class ServerEventRelay implements EventRelay {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private EventListenerRegistry eventListenerRegistry;
    private OutboundEventGateway outboundEventGateway;
    private MetricsServiceManager metricsManager;
    private MotechEventConfig motechEventConfig;

    private static final String MESSAGE_DESTINATION = "message-destination";

    @Autowired
    public ServerEventRelay(OutboundEventGateway outboundEventGateway, EventListenerRegistry eventListenerRegistry, MetricsServiceManager metricsManager, MotechEventConfig motechEventConfig) {
        this.outboundEventGateway = outboundEventGateway;
        this.eventListenerRegistry = eventListenerRegistry;
        this.metricsManager = metricsManager;
        this.motechEventConfig = motechEventConfig;
    }

    // @TODO either relayEvent should be made private, or this method moved out to it's own class.
    public void sendEventMessage(MotechEvent event) {
        Set<EventListener> listeners = eventListenerRegistry.getListeners(event.getSubject());
        if (log.isDebugEnabled()) {
            log.debug("found " + listeners.size() + " for " + event.getSubject() + " in " + eventListenerRegistry.toString());
        }
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("subject", event.getSubject());
        parameters.put("listeners", String.format("%d", listeners.size()));

        if (!listeners.isEmpty()) {
            try {
                outboundEventGateway.sendEventMessage(event);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw e;
            }
            if (metricsManager.isServiceAvailable()) {
                metricsManager.getService().logEvent("motech.event.published", parameters);
            }
        } else {
            if (metricsManager.isServiceAvailable()) {
                metricsManager.getService().logEvent("motech.event.not-published", parameters);
            }
        }
    }

    /**
     * Relay an event to all the listeners of that event.
     *
     * @param event event being relayed
     */
    public void relayEvent(MotechEvent event) {
        // Retrieve a list of listeners for the given event type
        if (eventListenerRegistry == null) {
            String errorMessage = "eventListenerRegistry == null";
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        if (event == null) {
            String errorMessage = "Invalid request to relay null event";
            log.warn(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        Set<EventListener> listeners = eventListenerRegistry.getListeners(event.getSubject());

        if (log.isDebugEnabled()) {
            log.debug("found " + listeners.size() + " for " + event.getSubject() + " in " + eventListenerRegistry.toString());
        }

        // Is this message destine for a specific listener?
        if (event.getParameters().containsKey(MESSAGE_DESTINATION)) {

            String messageDestination = (String) event.getParameters().get(MESSAGE_DESTINATION);

            for (EventListener listener : listeners) {
                if (listener.getIdentifier().equals(messageDestination)) {
                    MotechEvent e = event.copy(event.getSubject(), event.getParameters());
                    logTimeAndHandleEvent(event, listener, e);
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
                // Is there a way to get at a Sets elements other than an iterator?  I know there is only one
                logTimeAndHandleEvent(event, listeners.iterator().next(), event);
            }
        }

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("event", event.getSubject());
        parameters.put("listeners", String.format("%d", listeners.size()));
        if (metricsManager.isServiceAvailable()) {
            metricsManager.getService().logEvent("motech.event-relay.relayEvent", parameters);
        }
    }

    private void logTimeAndHandleEvent(MotechEvent event, EventListener listener, MotechEvent e) {
        if (metricsManager.isServiceAvailable()) {
            final long startTime = metricsManager.getService().startTimer();
            metricsManager.getService().logEvent(e.getSubject());
            handleEvent(listener, e);
            metricsManager.getService().stopTimer(listener.getIdentifier() + ".handler." + event.getSubject(), startTime);
        } else {
            log.warn(String.format("Time could not have been logged for %s event. Metrics service is unavailable.", event.getSubject()));
            handleEvent(listener, e);
        }
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
            enrichedEventMessage = event.copy(event.getSubject(), parameters);
            outboundEventGateway.sendEventMessage(enrichedEventMessage);
        }
    }

    private void handleEvent(EventListener listener, MotechEvent event) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Object target = MotechProxyUtils.getTargetIfProxied(listener);
            Thread.currentThread().setContextClassLoader(target.getClass().getClassLoader());
            listener.handle(event);

        } catch (Exception e) {
            log.debug("Handling error - " + e.getMessage());
            event.getParameters().put(MotechEvent.PARAM_INVALID_MOTECH_EVENT, Boolean.TRUE);
            event.getParameters().put(MESSAGE_DESTINATION, listener.getIdentifier());

            if (event.getMessageRedeliveryCount() == motechEventConfig.getMessageMaxRedeliveryCount()) {
                event.getParameters().put(MotechEvent.PARAM_DISCARDED_MOTECH_EVENT, Boolean.TRUE);
                log.info("Discarding Motech event " + event + ". Max retry count reached.");
                throw e;
            }
            event.incrementMessageRedeliveryCount();
            sendEventMessage(event);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }
}
