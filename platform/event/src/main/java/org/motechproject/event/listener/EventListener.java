package org.motechproject.event.listener;

import org.motechproject.event.MotechEvent;

public interface EventListener {

    /**
     * Handle an particular event that has been received
     *
     * @param event
     */
    void handle(MotechEvent event);

    /**
     * Retrieve a unique identifier/key for the given listener class. This identifier is used
     * when messages are destine for this specific listener type
     *
     * @return Unique listener identifier/key
     */
    String getIdentifier();
}
