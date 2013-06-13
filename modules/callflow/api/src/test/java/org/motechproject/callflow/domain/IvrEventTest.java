package org.motechproject.callflow.domain;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class IvrEventTest {

    @Test
    public void shouldReturnEndOfCallStatuses() {
        assertFalse(IvrEvent.Queued.isEndOfCall());
        assertFalse(IvrEvent.Ringing.isEndOfCall());
        assertFalse(IvrEvent.Initiated.isEndOfCall());
        assertFalse(IvrEvent.Dtmf.isEndOfCall());

        assertTrue(IvrEvent.Answered.isEndOfCall());
        assertTrue(IvrEvent.Hangup.isEndOfCall());
        assertTrue(IvrEvent.Disconnected.isEndOfCall());

        assertTrue(IvrEvent.Unanswered.isEndOfCall());
        assertTrue(IvrEvent.Missed.isEndOfCall());
        assertTrue(IvrEvent.Busy.isEndOfCall());

        assertTrue(IvrEvent.Failed.isEndOfCall());

        assertFalse(IvrEvent.DialInitiated.isEndOfCall());
        assertFalse(IvrEvent.DialRecord.isEndOfCall());

        assertFalse(IvrEvent.DialAnswered.isEndOfCall());

        assertFalse(IvrEvent.DialUnanswered.isEndOfCall());
        assertFalse(IvrEvent.DialMissed.isEndOfCall());
        assertFalse(IvrEvent.DialBusy.isEndOfCall());

        assertFalse(IvrEvent.DialFailed.isEndOfCall());
    }
}
