package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.SmsEventHandler;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SmsSendHandler implements SmsEventHandler {
    private ManagedSmslibService service;

    @Autowired
    public SmsSendHandler(ManagedSmslibService service) {
        this.service = service;
    }

    @Override
    @MotechListener(subjects = EventSubjects.SEND_SMS)
    public void handle(MotechEvent event) throws Exception {
        List<String> recipients = (List<String>) event.getParameters().get(EventDataKeys.RECIPIENTS);
        String text = (String) event.getParameters().get(EventDataKeys.MESSAGE);
        DateTime deliveryTime = (DateTime) event.getParameters().get(EventDataKeys.DELIVERY_TIME);

        if (deliveryTime == null) {
            service.queueMessage(recipients, text);
        } else {
            service.queueMessageAt(recipients, text, deliveryTime);
        }
    }
}
