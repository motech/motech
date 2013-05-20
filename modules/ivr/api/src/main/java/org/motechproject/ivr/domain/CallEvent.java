package org.motechproject.ivr.domain;

import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.Map;

public class CallEvent {

    private final MotechEvent motechEvent;

    public CallEvent(String callEvent, CallDetail callDetail) {
        Map<String, Object> params = new HashMap<>();
        params.put(EventKeys.CALL_DETAIL_RECORD_PARAM, callDetail);
        motechEvent = new MotechEvent(callEvent, params);
    }

    public CallDetail getCallDetail() {
        return (CallDetail) motechEvent.getParameters().get(EventKeys.CALL_DETAIL_RECORD_PARAM);
    }

    public MotechEvent toMotechEvent() {
        return motechEvent;
    }
}
