package org.motechproject.decisiontree.core;

import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.Map;

public class EndOfCallEvent extends MotechEvent {

    public EndOfCallEvent(CallDetail callDetail) {
        super(EventKeys.END_OF_CALL_EVENT, getPayload(callDetail));
    }

    public CallDetail getCallDetail() {
        return (CallDetail) parameters.get(EventKeys.CALL_DETAIL_RECORD_PARAM);
    }

    private static Map<String, Object> getPayload(CallDetail callDetail) {
        Map<String, Object> params = new HashMap<>();
        params.put(EventKeys.CALL_DETAIL_RECORD_PARAM, callDetail);
        return params;
    }
}
