package org.motechproject.server.event;

import org.motechproject.metrics.MetricsAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


/**
 * This class acts as a registry for all scheduled event listeners. One can register themselves to listen for
 * a specific set of event types.
 */
public class EventListenerRegistry {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private EventListenerTree listenerTree = new EventListenerTree();

    @Autowired
    private MetricsAgent metricsAgent;

    public EventListenerRegistry() {
    }

    public EventListenerRegistry(MetricsAgent metricsAgent) {
        this.metricsAgent = metricsAgent;
    }

    /**
     * Register an event listener to be notified when events of a given type are received via the Server JMS Event Queue
     *
     * @param listener the listener instance
     * @param subjects the event types that a listener is interested in
     */
    public void registerListener(EventListener listener, List<String> subjects) {

        if (listener == null) {
            String errorMessage = "Invalid attempt to register a null EventListener";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (subjects == null) {
            String errorMessage = "Invalid attempt to register for null subjects";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        // Add the listener to the  list of those interested in each event type
        for (String subject : subjects) {
            registerListener(listener, subject);
        }
    }

    public void registerListener(EventListener listener, String subject) {
        if (listener == null) {
            String errorMessage = "Invalid attempt to register a null EventListener";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (subject == null) {
            String errorMessage = "Invalid attempt to register for null subject";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        final long startTime = metricsAgent.startTimer();
        listenerTree.addListener(listener, subject);
        metricsAgent.stopTimer("motech.listener-registry.addListener", startTime);
    }

    /**
     * Retrieve a list of event listeners for a given event type. If there are no listeners, an empty list is
     * returned.
     *
     * @param subject The event type that you are seeking listeners for
     * @return A list of scheduled event listeners that are interested in that event
     */
    public Set<EventListener> getListeners(String subject) {
        final long startTime = metricsAgent.startTimer();
        Set<EventListener> ret = listenerTree.getListeners(subject);
        metricsAgent.stopTimer("motech.listener-registry.getListeners", startTime);

        return ret;
    }

    /**
     * See if a particular subject has any listeners
     *
     * @param subject
     * @return
     */
    public boolean hasListener(String subject) {

        final long startTime = metricsAgent.startTimer();
        boolean ret = listenerTree.hasListener(subject);
        metricsAgent.stopTimer("motech.listener-registry.hasListener", startTime);

        return ret;
    }

    /**
     * Get the count of listeners for a particular subject
     *
     * @param subject
     * @return
     */
    public int getListenerCount(String subject) {

        final long startTime = metricsAgent.startTimer();
        int ret = listenerTree.getListenerCount(subject);
        metricsAgent.stopTimer("motech.listener-registry.hasListener", startTime);

        return ret;
    }

    public void clearListenersForBean(String beanName) {
        listenerTree.removeAllListeners(beanName);
    }
}
