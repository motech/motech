package org.motechproject.event.listener;

import org.motechproject.event.MotechEvent;

/**
 * The <code>EventRelay</code> interface provides methods that allow sending {@link org.motechproject.event.MotechEvent}
 * via ActiveMQ, either to the queue (ActiveMQ selects the subscriber that will handle the event) or to the topic (event
 * is sent to every registered subscriber).
 */
public interface EventRelay {

    /**
     * Publishes the event message in a queue. The message goes a JMS queue, so if you have multiple Motech instances,
     * only one of them will be chosen by ActiveMQ as the recipient of the event. This mechanism allows achieving scalability in a cluster.
     * This is the method to use for sending your event, unless you are absolutely sure you want the event being processed
     * by all your nodes in the cluster simultaneously. The message is then handled by exactly one Motech instance, by calling
     * {@link org.motechproject.event.listener.impl.ServerEventRelay#relayQueueEvent(MotechEvent)} service method.
     * The message will only go to ActiveMQ if there are listeners registered for the subject (in this instance),
     * meaning if you have clustered Motech instances, you must ensure they all have the listeners registered.
     *
     * @param motechEvent the event to be sent
     */
    void sendEventMessage(MotechEvent motechEvent);

    /**
     * Publishes the event message in a topic. The message goes to a JMS topic, so if you have multiple Motech instances, they will
     * all receive the event. This allows broadcasting administration-type events that should be handled by each node separately.
     * This method should be only used if you are absolutely sure that the event should get processed by all your nodes in the cluster simultaneously.
     * The message is then handled by all Motech instances, by calling
     * {@link org.motechproject.event.listener.impl.ServerEventRelay#relayTopicEvent(MotechEvent)} service method.
     * The message will only go to ActiveMQ if there are listeners registered for the subject (in this instance),
     * meaning if you have clustered Motech instances, you must ensure they all have the listeners registered.
     *
     * @param motechEvent the event to be broadcast
     */
    void broadcastEventMessage(MotechEvent motechEvent);
}
