package org.motechproject.event.messaging.impl;

import org.motechproject.event.MotechEvent;

import java.util.UUID;

/**
 * Transforms <code>MotechEvent</code> by settings its UUID.
 */
public class MotechEventTransformer {

    /**
     * Updates the motechEvent's {@code UUID} with a random value
     * if it is null, otherwise it does not change it.
     *
     * @param motechEvent the motechEvent to be updated
     *
     * @return the motechEvent after being updated
     *
     * @see java.util.UUID#randomUUID()
     */
    public MotechEvent transform(MotechEvent motechEvent) {
        if (motechEvent.getId() == null) {
            motechEvent.setId(UUID.randomUUID());
        }
        return motechEvent;
    }
}
