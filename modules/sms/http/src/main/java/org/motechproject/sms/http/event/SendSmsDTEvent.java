package org.motechproject.sms.http.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendSmsDTEvent {

    private MotechEvent event;

    public SendSmsDTEvent(List<String> recipients, String message) {
        this(recipients, message, 0);
    }

    public SendSmsDTEvent(List<String> recipients, String message, Integer failureCount) {
        Map<String, Object> params = new HashMap<>();
        params.put(EventDataKeys.RECIPIENTS, recipients);
        params.put(EventDataKeys.MESSAGE, message);
        params.put(EventDataKeys.FAILURE_COUNT, failureCount);
        event = new MotechEvent(EventSubjects.SEND_SMSDT, params);
    }

    public SendSmsDTEvent(MotechEvent event) {
        this.event = event;
    }

    public MotechEvent toMotechEvent() {
        return event;
    }

    public List<String> getRecipients() {
        return (List<String>) event.getParameters().get(EventDataKeys.RECIPIENTS);
    }

    public String getMessage() {
        return (String) event.getParameters().get(EventDataKeys.MESSAGE);
    }

}
