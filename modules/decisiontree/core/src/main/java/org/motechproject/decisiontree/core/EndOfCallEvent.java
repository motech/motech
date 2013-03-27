package org.motechproject.decisiontree.core;

import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.Map;

public class EndOfCallEvent {

    private final MotechEvent motechEvent;

    public EndOfCallEvent(CallDetail callDetail) {
        Map<String, Object> params = new HashMap<>();
        params.put(EventKeys.CALL_DETAIL_RECORD_PARAM, callDetail);
        motechEvent = new MotechEvent(EventKeys.END_OF_CALL_EVENT, params);
    }

    public CallDetail getCallDetail() {
        return (CallDetail) motechEvent.getParameters().get(EventKeys.CALL_DETAIL_RECORD_PARAM);
    }

    public MotechEvent toMotechEvent() {
        return motechEvent;
    }
}
