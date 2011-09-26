package org.motechproject.ivr.kookoo;

import org.motechproject.eventtracking.domain.Event;

public class EndOfCallEvent implements Event {

    private final String callId;

    private final String referenceId;

    public EndOfCallEvent(String callId, String referenceId) {
        this.callId = callId;
        this.referenceId = referenceId;
    }

    public String getCallId() {
        return callId;
    }

    public String getReferenceId() {
        return referenceId;
    }
}
