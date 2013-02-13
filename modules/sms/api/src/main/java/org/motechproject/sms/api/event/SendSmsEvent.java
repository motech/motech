package org.motechproject.sms.api.event;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendSmsEvent {

    private MotechEvent event;

    public SendSmsEvent(List<String> recipients, String message, DateTime deliveryTime) {
        Map<String, Object> params = new HashMap<>();
        params.put(EventDataKeys.RECIPIENTS, recipients);
        params.put(EventDataKeys.MESSAGE, message);
        params.put(EventDataKeys.DELIVERY_TIME, deliveryTime);
        event = new MotechEvent(EventSubjects.SEND_SMS, params);
    }

    public SendSmsEvent(MotechEvent event) {
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

    public DateTime getDeliveryTime() {
        return (DateTime) event.getParameters().get(EventDataKeys.DELIVERY_TIME);
    }
}
