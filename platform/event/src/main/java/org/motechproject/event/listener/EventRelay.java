package org.motechproject.event.listener;

import org.motechproject.event.MotechEvent;

/**
 * Sends <code>MotechEvent</code> to the queue in ActiveMQ. The ActiveMQ then selects a subscriber(Motech instance)
 * that will handle the event and the {@link org.motechproject.event.listener.impl.ServerEventRelay#relayEvent(org.motechproject.event.MotechEvent)} is then called
 */
public interface EventRelay {

    /**
     * Publishes the event message. The message will only go to ActiveMQ if there are listeners
     * registered for the subject(in this instance). Meaning if you have clustered Motech instances,
     * you must ensure they both have the listeners registered.
     *
     * @param motechEvent the event to be sent
     */
    void sendEventMessage(MotechEvent motechEvent);
}
