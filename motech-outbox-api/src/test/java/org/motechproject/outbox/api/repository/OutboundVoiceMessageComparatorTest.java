package org.motechproject.outbox.api.repository;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.motechproject.outbox.api.domain.SortKey;

import static org.junit.Assert.assertThat;

public class OutboundVoiceMessageComparatorTest {

    @Test
    public void lookupComparatorBasedOnSortKey() {
        OutboundVoiceMessageComparator comparator = OutboundVoiceMessageComparator.getComparator(SortKey.CreationTime);
        assertThat(comparator, CoreMatchers.instanceOf(TimeBasedOutboundVoiceMessageComparator.class));

        comparator = OutboundVoiceMessageComparator.getComparator(SortKey.SequenceNumber);
        assertThat(comparator, CoreMatchers.instanceOf(SequenceBasedOutboundVoiceMessageComparator.class));
    }
}
