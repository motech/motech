package org.motechproject.sms.api.service;

import org.apache.log4j.Logger;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.api.EventKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SMSService {
    private static final Logger LOG = Logger.getLogger(SMSService.class);

    private EventRelay eventRelay;

    @Autowired
    public SMSService(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    public void sendSMS(String number, String text)
    {
        final HashMap eventData = new HashMap(2);
        eventData.put(EventKeys.NUMBER, number);
        eventData.put(EventKeys.MESSAGE, text);

        MotechEvent smsEvent = new MotechEvent(EventKeys.SEND_SMS, eventData);

        LOG.info(String.format("Putting event on relay to send message %s to number %s", text, number));
        eventRelay.sendEventMessage(smsEvent);
    }
}