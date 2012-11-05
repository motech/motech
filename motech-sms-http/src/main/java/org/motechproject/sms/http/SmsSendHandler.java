package org.motechproject.sms.http;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.SmsEventHandler;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.http.service.SmsHttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SmsSendHandler implements SmsEventHandler {

    private SmsHttpService smsHttpService;
    private static Logger log = LoggerFactory.getLogger(SmsSendHandler.class);

    @Autowired
    public SmsSendHandler(SmsHttpService smsHttpService) {
        this.smsHttpService = smsHttpService;
    }

    @Override
    @MotechListener(subjects = EventSubjects.SEND_SMS)
    public void handle(MotechEvent event) throws Exception {
        try {
        List<String> recipients = (List<String>) event.getParameters().get(EventDataKeys.RECIPIENTS);
        String message = (String) event.getParameters().get(EventDataKeys.MESSAGE);
        smsHttpService.sendSms(recipients, message);
        } catch (SmsDeliveryFailureException e) {
            log.error("Exception in delivering the SMS message.", e);
        }
    }
}
