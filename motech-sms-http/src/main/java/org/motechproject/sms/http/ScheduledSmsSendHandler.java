package org.motechproject.sms.http;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.SmsEventHandler;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.http.constants.EventSubjects;
import org.motechproject.sms.http.service.SmsHttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledSmsSendHandler implements SmsEventHandler {

    private SmsHttpService smsHttpService;

    @Autowired
    public ScheduledSmsSendHandler(SmsHttpService smsHttpService) {
        this.smsHttpService = smsHttpService;
    }

    @Override
    @MotechListener(subjects = EventSubjects.SCHEDULED_SEND_SMS)
    public void handle(MotechEvent event) throws SmsDeliveryFailureException {
        List<String> recipients = (List<String>) event.getParameters().get(EventDataKeys.RECIPIENTS);
        String message = (String) event.getParameters().get(EventDataKeys.MESSAGE);
        smsHttpService.sendSms(recipients, message);
    }
}
