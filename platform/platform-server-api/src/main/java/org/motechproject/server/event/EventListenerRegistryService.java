package org.motechproject.server.event;

import java.util.List;
import java.util.Set;

/**
 * This interface is necessary for OSGi service publication. The implementing
 * class acts as a registry for all scheduled event listeners. One can register
 * themselves to listen for a specific set of event types.
 */
public interface EventListenerRegistryService {

    /**
     * Register an event listener to be notified when events of a given type are
     * received via the Server JMS Event Queue.
     *
     * @param listener the listener instance
     * @param subjects the event types that a listener is interested in
     */
    void registerListener(EventListener listener, List<String> subjects);

    void registerListener(EventListener listener, String subject);

    /**
     * Retrieve a list of event listeners for a given event type. If there are
     * no listeners, an empty list is returned.
     *
     * @param subject The event type that you are seeking listeners for
     * @return A list of scheduled event listeners that are interested in that event
     */
    Set<EventListener> getListeners(String subject);

    /**
     * See if a particular subject has any listeners.
     *
     * @param subject
     * @return
     */
    boolean hasListener(String subject);

    /**
     * Get the count of listeners for a particular subject.
     *
     * @param subject
     * @return
     */
    int getListenerCount(String subject);

    /**
     * This method is responsible for removing listeners for a particular bean.
     * This is necessary when bundles are stopped in some fashion so that the
     * listener does not persist.
     *
     * @param beanName The bean name from the Spring context of the candidate class for listener clearing
     */
    void clearListenersForBean(String beanName);
}
