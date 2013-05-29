package org.motechproject.sms.http;

import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.sms.api.SmsDeliveryFailureException;
import org.motechproject.sms.api.SmsEventHandler;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.motechproject.sms.http.service.SmsHttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

import static org.motechproject.sms.api.DeliveryStatus.PENDING;
import static org.motechproject.sms.api.SMSType.OUTBOUND;

@Component
public class SmsSendHandler implements SmsEventHandler {
    private SmsHttpService smsHttpService;
    private SmsAuditService smsAuditService;

    private Random random = new Random();


    @Autowired
    public SmsSendHandler(SmsHttpService smsHttpService, SmsAuditService smsAuditService) {
        this.smsHttpService = smsHttpService;
        this.smsAuditService = smsAuditService;
    }

    @Override
    @MotechListener(subjects = {EventSubjects.SEND_SMS, EventSubjects.SEND_SMSDT})
    public void handle(MotechEvent event) throws SmsDeliveryFailureException {
        List<String> recipients = (List<String>) event.getParameters().get(EventDataKeys.RECIPIENTS);
        String message = (String) event.getParameters().get(EventDataKeys.MESSAGE);
        DateTime deliveryTime = (DateTime) event.getParameters().get(EventDataKeys.DELIVERY_TIME);

        for (String recipient : recipients) {
            smsAuditService.log(new SmsRecord(OUTBOUND, recipient, message, DateUtil.now(), PENDING, Integer.toString(random.nextInt())));
        }

        if (deliveryTime == null) {
            smsHttpService.sendSms(recipients, message);
        } else {
            smsHttpService.sendSms(recipients, message, deliveryTime);
        }

    }


}


