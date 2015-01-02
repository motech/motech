package org.motechproject.event.listener;

import org.motechproject.event.MotechEvent;

/**
 * Provides the base model interface for event listeners. In case of listeners using
 * annotations, proxies implementing this interface are created,
 * so there is no actual need to implement this interface when creating listeners.
 */
public interface EventListener {

    /**
     * Handles the particular event that has been received
     *
     * @param event the event that occurred.
     */
    void handle(MotechEvent event);

    /**
     * Returns the unique identifier/key for the given listener object. The identifier is used
     * when messages are destined for this specific listener type.
     *
     * @return the unique listener identifier/key
     */
    String getIdentifier();
}
