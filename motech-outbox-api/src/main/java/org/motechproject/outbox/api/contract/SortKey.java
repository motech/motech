package org.motechproject.outbox.api.contract;

import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.service.VoiceOutboxService;

/**
 * \ingroup outbox
 *
 * Represents the sorting behaviour for retrieval of outbox messages. {@link VoiceOutboxService#getNextMessage(String, OutboundVoiceMessageStatus)}
 */
public enum SortKey {
    /**
     * Represents the sorting behaviour to be based on sequence number in ascending order of the {@link OutboundVoiceMessage}
     */
    SequenceNumber,
    /**
     * Represents the sorting behaviour to be based on creation time (latest first) of the {@link OutboundVoiceMessage}.
     */
    CreationTime,
}
