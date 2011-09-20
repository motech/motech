package org.motechproject.ivr;

import org.joda.time.DateTime;
import org.motechproject.eventtracking.domain.Event;

import java.util.Map;

public class IVRCallEvent implements Event {
    private String sessionId;
    String eventName;
    String externalID;
    DateTime dateTime;
    Map<String, String> data;
    Map<String, String> requestParams;

    public IVRCallEvent() {
    }

    public IVRCallEvent(String sessionId, String eventName, String externalID, Map<String, String> requestParams,
                        DateTime dateTime, Map<String, String> data) {
        super();
        this.sessionId = sessionId;
        this.eventName = eventName;
        this.externalID = externalID;
        this.requestParams = requestParams;
        this.dateTime = dateTime;
        this.data = data;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
