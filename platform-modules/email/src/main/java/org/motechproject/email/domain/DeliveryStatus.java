package org.motechproject.email.domain;

/**
 * The <code>DeliveryStatus</code> Enum contains the possible delivery states for
 * an email message.
 */

public enum DeliveryStatus {
    /**
     * The message was sent.
     */
    SENT,

    /**
     * There was an error sending the message.
     */
    ERROR,

    /**
     * The message was received.
     */
    RECEIVED
}
