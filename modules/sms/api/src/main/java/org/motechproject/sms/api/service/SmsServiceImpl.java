package org.motechproject.sms.api.service;

import org.apache.log4j.Logger;
import org.hibernate.validator.ValidatorFactoryBean;
import org.joda.time.DateTime;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.api.MessageSplitter;
import org.motechproject.sms.api.event.SendSmsEvent;
import org.motechproject.sms.api.exceptions.SendSmsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

@Service
public class SmsServiceImpl implements SmsService {

    private EventRelay eventRelay;
    private MotechSchedulerService schedulerService;
    private MessageSplitter messageSplitter;
    private SettingsFacade settings;
    private Validator validator;

    private final Logger log = Logger.getLogger(SmsServiceImpl.class);

    private static final String PART_MESSAGE_HEADER_TEMPLATE = "Msg %d of %d: ";
    private static final String PART_MESSAGE_FOOTER = "...";

    public static final String SMS_MULTI_RECIPIENT_SUPPORTED = "sms.multi.recipient.supported";
    public static final String SMS_SCHEDULE_FUTURE_SMS = "sms.schedule.future.sms";
    public static final String SMS_MAX_MESSAGE_SIZE = "sms.max.message.size";

    @Autowired
    public SmsServiceImpl(MotechSchedulerService schedulerService, MessageSplitter messageSplitter, @Qualifier("smsApiSettings") SettingsFacade settings, EventRelay eventRelay) {
        this.schedulerService = schedulerService;
        this.messageSplitter = messageSplitter;
        this.settings = settings;
        this.eventRelay = eventRelay;
        this.validator = ValidatorFactoryBean.getInstance().getValidator();
    }

    @Override
    public void sendSMS(SendSmsRequest request) {
        Set<ConstraintViolation<SendSmsRequest>> violations = validator.validate(request);
        if (violations != null && violations.size() > 0) {
            throw new SendSmsException(new IllegalArgumentException(getExceptionMessage(violations)));
        }
        final List<String> recipients = request.getRecipients();
        final DateTime deliveryTime = request.getDeliveryTime();
        int partMessageSize = getIntegerPropertyValue(SMS_MAX_MESSAGE_SIZE);
        boolean isMultiRecipientSupported = getBooleanPropertyValue(SMS_MULTI_RECIPIENT_SUPPORTED);

        List<String> partMessages = messageSplitter.split(request.getMessage(), partMessageSize, PART_MESSAGE_HEADER_TEMPLATE, PART_MESSAGE_FOOTER);
        if (isMultiRecipientSupported) {
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
        boolean shouldScheduleFutureSms = getBooleanPropertyValue(SMS_SCHEDULE_FUTURE_SMS);
        for (String partMessage : partMessages) {
            if (shouldScheduleFutureSms && deliveryTime != null) {
                scheduleSendSmsEvent(recipients, partMessage, deliveryTime);
            } else {
                raiseSendSmsEvent(recipients, partMessage, deliveryTime);
            }
        }
    }

    private boolean getBooleanPropertyValue(String property) {
        return Boolean.valueOf(settings.getProperty(property));
    }

    private int getIntegerPropertyValue(String property) {
        String value = settings.getProperty(property);
        return value == null ? 0 : Integer.parseInt(value);
    }

    private void raiseSendSmsEvent(List<String> recipients, String message, DateTime deliveryTime) {
        log.info(String.format("Sending message [%s] to number %s.", message, recipients));
        eventRelay.sendEventMessage(new SendSmsEvent(recipients, message, deliveryTime).toMotechEvent());
    }

    private void scheduleSendSmsEvent(final List<String> recipients, final String message, final DateTime deliveryTime) {
        RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(new SendSmsEvent(recipients, message, deliveryTime).toMotechEvent(), deliveryTime.toDate());
        log.info(String.format("Scheduling message [%s] to number %s at %s.", message, recipients, deliveryTime.toString()));
        schedulerService.safeScheduleRunOnceJob(schedulableJob);
    }

    private String getExceptionMessage(Set<ConstraintViolation<SendSmsRequest>> violations) {
        String message = "";
        for (ConstraintViolation<SendSmsRequest> violation : violations) {
            message += format("%s %s; ", violation.getPropertyPath().toString(), violation.getMessage());
        }
        return message;
    }
}
