package org.motechproject.sms.api.service;

import org.apache.log4j.Logger;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmsService {
    public static final String SEND_SMS = "SendSMS";
    public static final String RECIPIENTS = "number";
    public static final String MESSAGE = "message";

    private static final Logger log = Logger.getLogger(SmsService.class);

    private EventRelay eventRelay;

    @Autowired
    public SmsService(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    public void sendSMS(List<String> recipients, String message) {
        Map<String, Object> eventData = new HashMap<String, Object>();
        eventData.put(RECIPIENTS, recipients);
        eventData.put(MESSAGE, message);

        MotechEvent smsEvent = new MotechEvent(SEND_SMS, eventData);

        log.info(String.format("Putting event on relay to send message %s to number %s", message, recipients));
        eventRelay.sendEventMessage(smsEvent);
    }

    public void sendSMS(String recipient, String message) {
        sendSMS(Arrays.asList(recipient), message);
    }
}