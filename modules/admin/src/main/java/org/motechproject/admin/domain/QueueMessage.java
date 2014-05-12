package org.motechproject.admin.domain;

import java.util.Date;

/**
 * Represents a message from the JMS queue.
 */
public class QueueMessage {
    private String messageId;
    private final Boolean redelivered;
    private final Date timestamp;

    public QueueMessage(String messageId, Boolean redelivered, Date timestamp) {
        this.messageId = messageId;
        this.redelivered = redelivered;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public Boolean getRedelivered() {
        return redelivered;
    }

    public String getTimestamp() {
        return timestamp.toString();
    }
}
