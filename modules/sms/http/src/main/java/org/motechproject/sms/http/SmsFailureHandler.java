package org.motechproject.sms.http;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.api.SmsDeliveryFailureException;
import org.motechproject.sms.api.SmsEventHandler;
import org.motechproject.sms.api.constants.EventSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.motechproject.sms.api.constants.EventDataKeys.FAILURE_COUNT;
import static org.motechproject.sms.api.constants.EventDataKeys.MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.RECIPIENT;
import static org.motechproject.sms.api.constants.EventDataKeys.RECIPIENTS;

@Component
public class SmsFailureHandler implements SmsEventHandler {
    private EventRelay eventRelay;

    private int maxRetries;

    @Autowired
    public SmsFailureHandler(EventRelay eventRelay, @Qualifier("smsApiSettings") SettingsFacade settings) {
        this.eventRelay = eventRelay;

        String maxRetriesAsString = settings.getProperty("max_retries");
        this.maxRetries = maxRetriesAsString != null ? Integer.parseInt(maxRetriesAsString) : 0;
    }

    @Override
    @MotechListener(subjects = {EventSubjects.SMS_FAILURE_NOTIFICATION})
    public void handle(MotechEvent event) throws SmsDeliveryFailureException {
        String recipient = String.valueOf(event.getParameters().get(RECIPIENT));
        String message = String.valueOf(event.getParameters().get(MESSAGE));
        Integer failureCount = (Integer) event.getParameters().get(FAILURE_COUNT);

        if (failureCount <= maxRetries) {
            Map<String, Object> param = new HashMap<>();
            param.put(RECIPIENTS, Arrays.asList(recipient));
            param.put(MESSAGE, message);
            param.put(FAILURE_COUNT, failureCount);

            eventRelay.sendEventMessage(new MotechEvent(EventSubjects.SEND_SMS, param));
        }
    }
}
