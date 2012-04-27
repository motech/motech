package org.motechproject.outbox.api.repository;

import org.motechproject.outbox.api.domain.OutboundVoiceMessage;

class TimeBasedOutboundVoiceMessageComparator extends OutboundVoiceMessageComparator
{
    @Override
    public int compare(OutboundVoiceMessage m1, OutboundVoiceMessage m2) {
        if (m1.getCreationTime() == null) {
            throw new InvalidDataException("Invalid object: " + m1 + " Creation time in OutboundVoiceMessage can not be null");
        }

        if (m2.getCreationTime() == null) {
            throw new InvalidDataException("Invalid object: " + m2 + " Creation time in OutboundVoiceMessage can not be null");
        }
        return m2.getCreationTime().compareTo(m1.getCreationTime());
    }

}
