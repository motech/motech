package org.motechproject.ivr.kookoo;

import org.motechproject.eventtracking.domain.Event;

public class EndOfCallEvent implements Event {

    private final String callId;

    private final String externalId;

    public EndOfCallEvent(String callId, String externalId) {
        this.callId = callId;
        this.externalId = externalId;
    }

    public String getCallId() {
        return callId;
    }

    public String getExternalId() {
        return externalId;
    }
}
