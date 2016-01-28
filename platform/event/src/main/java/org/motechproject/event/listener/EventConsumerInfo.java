package org.motechproject.event.listener;

/**
 * The <code>EventConsumerInfo</code> interface provides methods for getting information about ActiveMQ Event Consumers.
 */
public interface EventConsumerInfo {

    /**
     * Checks if ActiveMQ Event Consumers are running
     *
     * @return true if Event Consumers are running, false otherwise
     */
    boolean isRunning();
}
