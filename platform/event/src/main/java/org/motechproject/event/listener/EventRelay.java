package org.motechproject.event.listener;

import org.motechproject.event.MotechEvent;

/**
 * The <code>EventRelay</code> interface provides methods that allow sending {@link org.motechproject.event.MotechEvent}
 * via ActiveMQ, either to the queue (ActiveMQ selects the subscriber that will handle the event) or to the topic (event
 * is sent to every registered subscriber).
 *
 */
public interface EventRelay {

    /**
     * Publishes the event message in a queue. The message will only go to ActiveMQ if there are listeners
     * registered for the subject (in this instance). Meaning if you have clustered Motech instances,
     * you must ensure they both have the listeners registered. The message is then handled by exactly one
     * Motech instance, by calling {@link org.motechproject.event.listener.impl.ServerEventRelay#relayQueueEvent(org.motechproject.event.MotechEvent)}
     * service method.
     *
     * @param motechEvent the event to be sent
     */
    void sendEventMessage(MotechEvent motechEvent);

    /**
     * Publishes the event message in a topic. The message will only go to ActiveMQ if there are listeners
     * registered for the subject (in this instance). Meaning if you have clustered Motech instances,
     * you must ensure they both have the listeners registered. The message is then handled by all Motech instances
     * that are subscribed to a topic, by calling {@link org.motechproject.event.listener.impl.ServerEventRelay#relayTopicEvent(org.motechproject.event.MotechEvent)}
     * service method. This method should only be used when it is desired to broadcast an event to all Motech instances.
     *
     * @param motechEvent the event to be broadcast
     */
    void broadcastEventMessage(MotechEvent motechEvent);
}
