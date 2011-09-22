package org.motechproject.ivr;

import org.joda.time.DateTime;
import org.motechproject.eventtracking.domain.Event;

import java.util.Map;

public class IVRCallEvent implements Event {

    private String callId;

    private String callEvent;

    private String externalID;

    private DateTime dateTime;

    private Map<String, String> data;

    private Map<String, String> requestParams;

    public IVRCallEvent() {
    }

    public IVRCallEvent(String callId, String callEvent, String externalID, Map<String, String> requestParams,
                        DateTime dateTime, Map<String, String> data) {
        super();
        this.callId = callId;
        this.callEvent = callEvent;
        this.externalID = externalID;
        this.requestParams = requestParams;
        this.dateTime = dateTime;
        this.data = data;
    }

    public String getCallEvent() {
        return callEvent;
    }

    public void setCallEvent(String callEvent) {
        this.callEvent = callEvent;
    }

    public String getExternalID() {
        return externalID;
    }

    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, String> getRequestParams() {
        return requestParams;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

}
