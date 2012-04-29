package org.motechproject.outbox.api.repository;

import org.motechproject.outbox.api.domain.OutboundVoiceMessage;

import java.util.Comparator;

abstract class OutboundVoiceMessageComparator implements Comparator<OutboundVoiceMessage> {

    static OutboundVoiceMessageComparator getComparator(SortKey sortKey) {
        if(sortKey.isCreationTime())
            return new TimeBasedOutboundVoiceMessageComparator();
        return new SequenceBasedOutboundVoiceMessageComparator();
    }
}