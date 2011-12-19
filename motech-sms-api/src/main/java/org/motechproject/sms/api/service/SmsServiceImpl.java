package org.motechproject.sms.api.service;

import org.apache.log4j.Logger;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.api.constants.EventSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {
    public static final String RECIPIENTS = "number";
    public static final String MESSAGE = "message";

    private static final Logger log = Logger.getLogger(SmsServiceImpl.class);

    private EventRelay eventRelay;

    @Autowired
    public SmsServiceImpl(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    @Override
    public void sendSMS(List<String> recipients, String message) {
        Map<String, Object> eventData = new HashMap<String, Object>();
        eventData.put(RECIPIENTS, recipients);
        eventData.put(MESSAGE, message);

        MotechEvent smsEvent = new MotechEvent(EventSubject.SEND_SMS, eventData);

        log.info(String.format("Putting event on relay to send message %s to number %s", message, recipients));
        eventRelay.sendEventMessage(smsEvent);
    }

    @Override
    public void sendSMS(String recipient, String message) {
        sendSMS(Arrays.asList(recipient), message);
    }
}