package org.motechproject.outbox.api.repository;

import org.junit.Test;
import org.motechproject.outbox.api.builder.OutboundVoiceMessageBuilder;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class SequenceBasedOutboundVoiceMessageComparatorTest {

    @Test
    public void sortBasedOnSequenceNumber() {

        OutboundVoiceMessage message1 = new OutboundVoiceMessageBuilder().withDefaults().withSequenceNumber(3).build();
        OutboundVoiceMessage message2 = new OutboundVoiceMessageBuilder().withDefaults().withSequenceNumber(1).build();

        SequenceBasedOutboundVoiceMessageComparator comparator = new SequenceBasedOutboundVoiceMessageComparator();

        assertThat(comparator.compare(message1, message2), greaterThan(0));
        assertThat(comparator.compare(message1, message1), is(0));
        assertThat(comparator.compare(message2, message1), lessThan(0));
    }
}
