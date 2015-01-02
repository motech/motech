package org.motechproject.event.listener;

import java.util.List;
import java.util.Set;

/**
 * Gives access to the registry of listeners for Motech events. This interface is necessary for OSGi service publication.
 * One can register themselves to listen for a specific set of event's subject.
 */
public interface EventListenerRegistryService {

    /**
     * Registers the event listener to be notified when events with the matching
     * subject are received via the Server JMS Event Queue.
     *
     * @param listener the listener to be registered
     * @param subjects the list of subjects the listener subscribes to, wildcards are allowed
     */
    void registerListener(EventListener listener, List<String> subjects);

    /**
     * Registers the event listener to be notified when the event's subjects are
     * received via the Server JMS Event Queue.
     *
     * @param listener the listener to be registered
     * @param subject the subject the listener subscribes to, wildcards are allowed
     */
    void registerListener(EventListener listener, String subject);

    /**
     * Returns all the event listeners registered for the event with the given subject.
     * If there are no listeners, an empty list is returned.
     *
     * @param subject the subject of the event
     * @return the matching event listeners
     */
    Set<EventListener> getListeners(String subject);

    /**
     * Returns {@code true} if the event with the subject has any listeners.
     *
     * @param subject the subject of the event
     * @return {@code true} if the subject has any listeners; {@code false} otherwise
     */
    boolean hasListener(String subject);

    /**
     * Returns the number of event listeners for the event with the subject.
     *
     * @param subject the subject of the event
     * @return the number of matching listeners
     */
    int getListenerCount(String subject);

    /**
     * Removes all listeners registered in the bean. This is necessary when
     * bundles are stopped in some fashion so that the listener does not persist.
     *
     * @param beanName the name of the bean
     */
    void clearListenersForBean(String beanName);
}
