package org.motechproject.admin.domain;

import org.joda.time.DateTime;

/**
 * Represents a message from the JMS queue.
 */
public class QueueMessage {
    private String messageId;
    private final Boolean redelivered;
    private final DateTime timestamp;

    /**
     * Constructor.
     * @param messageId unique identifier for the message
     * @param redelivered whether the message was delivered
     * @param timestamp the timestamp of when the message was sent
     */
    public QueueMessage(String messageId, Boolean redelivered, DateTime timestamp) {
        this.messageId = messageId;
        this.redelivered = redelivered;
        this.timestamp = timestamp;
    }

    /**
     * @return unique identifier for the message
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @return true if the message is being resent to the consumer
     */
    public Boolean getRedelivered() {
        return redelivered;
    }

    /**
     * @return the timestamp of when the message was sent
     */
    public String getTimestamp() {
        return timestamp.toString();
    }
}
