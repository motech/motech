package org.motechproject.outbox.api.repository;

import org.motechproject.outbox.api.domain.OutboundVoiceMessage;

class SequenceBasedOutboundVoiceMessageComparator extends OutboundVoiceMessageComparator{
    @Override
    public int compare(OutboundVoiceMessage message1, OutboundVoiceMessage message2) {
        return new Long(message1.getSequenceNumber()).compareTo(message2.getSequenceNumber());
    }
}
