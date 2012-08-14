package org.motechproject.server.verboice.domain;

import java.util.Map;

@Deprecated
public class VerboiceAction {

    private String from;
    private String callSid;

    public VerboiceAction(Map<String, String> attributes) {
        from = attributes.get("From");
        callSid = attributes.get("CallSid");
    }

    public String getFrom() {
        return from;
    }

    public String getCallSid() {
        return callSid;
    }

}
