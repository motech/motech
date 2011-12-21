package org.motechproject.sms.api.service;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.context.EventContext;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.api.constants.EventSubject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class SmsServiceImpl implements SmsService {

    public static final String RECIPIENTS = "number";
    public static final String MESSAGE = "message";
    public static final String DELIVERY_TIME = "delivery_time";

    private EventRelay eventRelay;
    private static final Logger log = Logger.getLogger(SmsServiceImpl.class);

    public SmsServiceImpl() {
        this.eventRelay = EventContext.getInstance().getEventRelay();
    }

    @Override
    public void sendSMS(String recipient, String message) {
        raiseSendSmsEvent(Arrays.asList(recipient), message, null);
    }

    @Override
    public void sendSMS(List<String> recipients, String message) {
        raiseSendSmsEvent(recipients, message, null);
    }

    @Override
    public void sendSMS(String recipient, String message, DateTime deliveryTime) {
        raiseSendSmsEvent(Arrays.asList(recipient), message, deliveryTime);
    }

    @Override
    public void sendSMS(ArrayList<String> recipients, String message, DateTime deliveryTime) {
        raiseSendSmsEvent(recipients, message, deliveryTime);
    }

    private void raiseSendSmsEvent(final List<String> recipients, final String message, final DateTime deliveryTime) {
        log.info(String.format("Putting event on relay to send message %s to number %s", message, recipients));
        eventRelay.sendEventMessage(new MotechEvent(EventSubject.SEND_SMS, new HashMap<String, Object>() {{
            put(RECIPIENTS, recipients);
            put(MESSAGE, message);
            put(DELIVERY_TIME, deliveryTime);
        }}));
    }
}