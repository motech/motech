package org.motechproject.outbox.api.contract;


/**
 * \ingroup outbox
 *
 * Represents the sorting behaviour for retrieval of outbox messages. {@link org.motechproject.outbox.api.service.VoiceOutboxService#getNextMessage(String, OutboundVoiceMessageStatus)}
 */
public enum SortKey {
    /**
     * Represents the sorting behaviour to be based on sequence number in ascending order of the {@link org.motechproject.outbox.api.domain.OutboundVoiceMessage OutboundVoiceMessage}
     */
    SequenceNumber,
    /**
     * Represents the sorting behaviour to be based on creation time (latest first) of the {@link org.motechproject.outbox.api.domain.OutboundVoiceMessage OutboundVoiceMessage}
     */
    CreationTime,
}
