package org.motechproject.sms.smpp;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.SMSType;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.smslib.AGateway;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.sms.api.DeliveryStatus.ABORTED;
import static org.motechproject.sms.api.DeliveryStatus.KEEPTRYING;
import static org.motechproject.sms.api.SMSType.OUTBOUND;
import static org.motechproject.sms.api.constants.EventDataKeys.FAILURE_COUNT;
import static org.motechproject.sms.api.constants.EventDataKeys.MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.RECIPIENT;
import static org.motechproject.sms.api.constants.EventSubjects.SMS_FAILURE_NOTIFICATION;
import static org.motechproject.sms.smpp.constants.SmsProperties.MAX_RETRIES;

@Component
public class OutboundMessageNotification implements IOutboundMessageNotification {

    private final Logger log = Logger.getLogger(OutboundMessageNotification.class);
    private EventRelay eventRelay;
    private final Integer maxRetries;

    @Autowired
    private SmsAuditService smsAuditService;

    @Autowired
    public OutboundMessageNotification(EventRelay eventRelay, @Qualifier("smsApiSettings") SettingsFacade settings) {
        this.eventRelay = eventRelay;
        String maxRetriesAsString = settings.getProperty(MAX_RETRIES);
        this.maxRetries = maxRetriesAsString != null ? Integer.parseInt(maxRetriesAsString) : 0;
    }

    @Override
    public void process(AGateway gateway, OutboundMessage msg) {
        log.info(String.format("[%s] Outbound notification for (%s) with message (%s) received from gateway (%s)", msg.getMessageStatus().toString(), msg.getRecipient(), msg.getText(), gateway.getGatewayId()));

        DateTime sentTime = newDateTime(msg.getDate());
        if (sendingFailed(msg)) {
            if (msg.getRetryCount() >= maxRetries) {
                raiseFailureEvent(msg, sentTime);
            } else if (msg.getRetryCount() < maxRetries) {
                smsAuditService.log(new SmsRecord(OUTBOUND, msg.getRecipient(), msg.getText(), sentTime, KEEPTRYING, msg.getRefNo()));
            }
        } else {
            smsAuditService.log(new SmsRecord(SMSType.OUTBOUND, msg.getRecipient(), msg.getText(), sentTime, DeliveryStatus.DISPATCHED, msg.getRefNo()));
        }
    }

    private void raiseFailureEvent(OutboundMessage msg, DateTime sentTime) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(RECIPIENT, msg.getRecipient());
        parameters.put(MESSAGE, msg.getText());
        parameters.put(FAILURE_COUNT, msg.getRetryCount());
        eventRelay.sendEventMessage(new MotechEvent(SMS_FAILURE_NOTIFICATION, parameters));
        smsAuditService.log(new SmsRecord(OUTBOUND, msg.getRecipient(), msg.getText(), sentTime, ABORTED, msg.getRefNo()));
    }

    private boolean sendingFailed(OutboundMessage msg) {
        return msg.getMessageStatus().equals(OutboundMessage.MessageStatuses.FAILED);
    }
}
