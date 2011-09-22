package org.motechproject.ivr.eventlogging;

import org.joda.time.DateTime;
import org.motechproject.eventtracking.domain.Event;
import org.motechproject.ivr.IVRCallEvent;
import org.motechproject.server.service.ivr.IVRRequest;

import java.util.HashMap;
import java.util.Map;

public class IVRCallEventBuilder {

    private IVRCallEvent callEvent;

    Map<String, String> eventData = new HashMap<String, String>();

    public IVRCallEventBuilder(String callId, String externalId, String callEventName, Map<String, String> requestParams, DateTime dateTime) {
        callEvent = new IVRCallEvent(callId, callEventName, externalId, requestParams, dateTime, eventData);
    }

    public IVRCallEventBuilder withCallerId(String callerId) {
        eventData.put(EventLogConstants.CALLER_ID, callerId);
        return this;
    }

    public IVRCallEventBuilder withCallDirection(IVRRequest.CallDirection callDirection) {
        eventData.put(EventLogConstants.CALL_DIRECTION, callDirection.name());
        return this;
    }

    public IVRCallEventBuilder withResponseXML(String responseXML) {
        eventData.put(EventLogConstants.RESPONSE_XML, responseXML);
        return this;
    }

    public IVRCallEventBuilder withData(Map<String, String> eventData) {
        for (String key : eventData.keySet()) {
            this.eventData.put(key, eventData.get(key));
        }
        return this;
    }

    public Event build() {
        return callEvent;
    }
}
