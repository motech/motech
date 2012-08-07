package org.motechproject.outbox.api.domain;


/**
 * Holds the possible statuses of the voice outbox message {@link OutboundVoiceMessage}
 */
public enum OutboundVoiceMessageStatus {
    /**
     * Represents that the message is yet to be played
     */
    PENDING,
    /**
     * Represents that the message has been played
     */
    PLAYED,
    /**
     * Represents that the message has been saved to re-play later
     */
    SAVED
}
