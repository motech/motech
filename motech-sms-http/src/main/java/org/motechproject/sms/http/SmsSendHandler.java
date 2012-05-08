package org.motechproject.sms.http;

import org.joda.time.DateTime;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.SmsEventHandler;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.http.service.SmsHttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SmsSendHandler implements SmsEventHandler {

    private SmsHttpService smsHttpService;
    private MotechSchedulerService motechSchedulerService;

    @Autowired
    public SmsSendHandler(SmsHttpService smsHttpService, MotechSchedulerService motechSchedulerService) {
        this.smsHttpService = smsHttpService;
        this.motechSchedulerService = motechSchedulerService;
    }

    @Override
    @MotechListener(subjects = EventSubjects.SEND_SMS)
    public void handle(MotechEvent event) throws SmsDeliveryFailureException {

        List<String> recipients = (List<String>) event.getParameters().get(EventDataKeys.RECIPIENTS);
        DateTime deliveryTime = (DateTime) event.getParameters().get(EventDataKeys.DELIVERY_TIME);
        String message = (String) event.getParameters().get(EventDataKeys.MESSAGE);

        if(deliveryTime == null)
            smsHttpService.sendSms(recipients, message);
        else {
            MotechEvent scheduledSendEvent = new MotechEvent(org.motechproject.sms.http.constants.EventSubjects.SCHEDULED_SEND_SMS, event.getParameters());
            RunOnceSchedulableJob job = new RunOnceSchedulableJob(scheduledSendEvent, deliveryTime.toDate());
            motechSchedulerService.safeScheduleRunOnceJob(job);
        }
    }
}
