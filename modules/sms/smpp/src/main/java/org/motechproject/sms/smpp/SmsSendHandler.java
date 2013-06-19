package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.sms.api.SmsEventHandler;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

import static org.motechproject.sms.api.DeliveryStatus.PENDING;
import static org.motechproject.sms.api.SMSType.OUTBOUND;

@Component
public class SmsSendHandler implements SmsEventHandler {
    private ManagedSmslibService service;
    private SmsAuditService smsAuditService;

    private Random random = new Random();

    @Autowired
    public SmsSendHandler(ManagedSmslibService service, SmsAuditService smsAuditService) {
        this.service = service;
        this.smsAuditService = smsAuditService;
    }

    @Override
    @MotechListener(subjects = EventSubjects.SEND_SMS)
    public void handle(MotechEvent event) {
        List<String> recipients = (List<String>) event.getParameters().get(EventDataKeys.RECIPIENTS);
        String text = (String) event.getParameters().get(EventDataKeys.MESSAGE);
        DateTime deliveryTime = (DateTime) event.getParameters().get(EventDataKeys.DELIVERY_TIME);

        for (String recipient : recipients) {
            smsAuditService.log(new SmsRecord(
                    OUTBOUND, recipient, text, DateUtil.now(), PENDING,
                    Integer.toString(Math.abs(random.nextInt()))
            ));
        }

        if (deliveryTime == null) {
            service.queueMessage(recipients, text);
        } else {
            service.queueMessageAt(recipients, text, deliveryTime);
        }
    }
}
