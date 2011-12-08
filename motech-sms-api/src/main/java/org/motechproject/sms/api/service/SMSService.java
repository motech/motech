package org.motechproject.sms.api.service;

import org.apache.log4j.Logger;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.api.EventKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SMSService {
    private static final Logger LOG = Logger.getLogger(SMSService.class);

    private EventRelay eventRelay;

    @Autowired
    public SMSService(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    public void sendSMS(List<String> recipients, String message) {
        Map<String, Object> eventData = new HashMap<String, Object>();
        eventData.put(EventKeys.RECIPIENTS, recipients);
        eventData.put(EventKeys.MESSAGE, message);

        MotechEvent smsEvent = new MotechEvent(EventKeys.SEND_SMS, eventData);

        LOG.info(String.format("Putting event on relay to send message %s to number %s", message, recipients));
        eventRelay.sendEventMessage(smsEvent);
    }

    public void sendSMS(String recipient, String message) {
        sendSMS(Arrays.asList(recipient), message);
    }
}