package org.motechproject.server.verboice.domain;

import com.google.gson.annotations.SerializedName;

public class VerboiceServerResponse {

    @SerializedName("call_id")
    private String callId;
    private String state;

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
