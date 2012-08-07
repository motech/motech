package org.motechproject.sms.api.service;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.context.EventContext;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.scheduler.event.EventRelay;
import org.motechproject.sms.api.MessageSplitter;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@Service
public class SmsServiceImpl implements SmsService {
    private EventRelay eventRelay;
    private MotechSchedulerService motechSchedulerService;
    private MessageSplitter messageSplitter;
    private Properties smsApiProperties;

    private final Logger log = Logger.getLogger(SmsServiceImpl.class);

    private static final int PART_MESSAGE_SIZE = 160;
    private static final String PART_MESSAGE_HEADER_TEMPLATE = "Msg %d of %d: ";
    private static final String PART_MESSAGE_FOOTER = "...";

    public static final String SMS_MULTI_RECIPIENT_SUPPORTED = "sms.multi.recipient.supported";
    public static final String SMS_SCHEDULE_FUTURE_SMS = "sms.schedule.future.sms";

    @Autowired
    public SmsServiceImpl(MotechSchedulerService motechSchedulerService, MessageSplitter messageSplitter, Properties smsApiProperties) {
        this.motechSchedulerService = motechSchedulerService;
        this.messageSplitter = messageSplitter;
        this.smsApiProperties = smsApiProperties;
        this.eventRelay = EventContext.getInstance().getEventRelay();
    }

    @Override
    public void sendSMS(String recipient, String message) {
        scheduleOrRaiseSendSmsEvent(Arrays.asList(recipient), message, null);
    }

    @Override
    public void sendSMS(List<String> recipients, String message) {
        scheduleOrRaiseSendSmsEvent(recipients, message, null);
    }

    @Override
    public void sendSMS(String recipient, String message, DateTime deliveryTime) {
        scheduleOrRaiseSendSmsEvent(Arrays.asList(recipient), message, deliveryTime);
    }

    @Override
    public void sendSMS(ArrayList<String> recipients, String message, DateTime deliveryTime) {
        scheduleOrRaiseSendSmsEvent(recipients, message, deliveryTime);
    }

    private void scheduleOrRaiseSendSmsEvent(final List<String> recipients, final String message, final DateTime deliveryTime) {
        List<String> partMessages = messageSplitter.split(message, PART_MESSAGE_SIZE, PART_MESSAGE_HEADER_TEMPLATE, PART_MESSAGE_FOOTER);
        if (getBooleanPropertyValue(SMS_MULTI_RECIPIENT_SUPPORTED)) {
            generateOneSendSmsEvent(recipients, partMessages, deliveryTime);
        } else {
            generateSendSmsEventsForEachRecipient(recipients, partMessages, deliveryTime);
        }
    }

    private void generateSendSmsEventsForEachRecipient(List<String> recipients, List<String> partMessages, DateTime deliveryTime) {
        for (String recipient : recipients) {
            generateOneSendSmsEvent(Arrays.asList(recipient), partMessages, deliveryTime);
        }
    }

    private void generateOneSendSmsEvent(List<String> recipients, List<String> partMessages, DateTime deliveryTime) {
        for (String partMessage : partMessages) {
            if (getBooleanPropertyValue(SMS_SCHEDULE_FUTURE_SMS) && deliveryTime != null) {
                scheduleSendSmsEvent(recipients, partMessage, deliveryTime);
            } else {
                raiseSendSmsEvent(recipients, partMessage, deliveryTime);
            }
        }
    }

    private boolean getBooleanPropertyValue(String property) {
        return Boolean.valueOf(smsApiProperties.getProperty(property));
    }

    private void raiseSendSmsEvent(List<String> recipients, String message, DateTime deliveryTime) {
        log.info(String.format("Sending message [%s] to number %s.", message, recipients));
        eventRelay.sendEventMessage(sendSmsEvent(recipients, message, deliveryTime));
    }

    private void scheduleSendSmsEvent(final List<String> recipients, final String message, final DateTime deliveryTime) {
        MotechEvent sendSmsEvent = sendSmsEvent(recipients, message, deliveryTime);
        RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(sendSmsEvent, deliveryTime.toDate());
        log.info(String.format("Scheduling message [%s] to number %s at %s.", message, recipients, deliveryTime.toString()));
        motechSchedulerService.safeScheduleRunOnceJob(schedulableJob);
    }

    private MotechEvent sendSmsEvent(List<String> recipients, String message, DateTime deliveryTime) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventDataKeys.RECIPIENTS, recipients);
        parameters.put(EventDataKeys.MESSAGE, message);
        parameters.put(EventDataKeys.DELIVERY_TIME, deliveryTime);
        return new MotechEvent(EventSubjects.SEND_SMS, parameters);
    }
}
