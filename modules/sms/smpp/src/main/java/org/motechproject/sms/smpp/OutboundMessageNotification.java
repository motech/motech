package org.motechproject.sms.smpp;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.smpp.constants.EventSubjects;
import org.motechproject.sms.smpp.constants.SmsProperties;
import org.motechproject.sms.smpp.repository.AllOutboundSMS;
import org.smslib.AGateway;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static org.motechproject.sms.api.DeliveryStatus.ABORTED;
import static org.motechproject.sms.api.DeliveryStatus.INPROGRESS;
import static org.motechproject.sms.api.DeliveryStatus.KEEPTRYING;
import static org.motechproject.sms.api.constants.EventDataKeys.MESSAGE;
import static org.motechproject.sms.smpp.constants.EventDataKeys.RECIPIENT;
import static org.motechproject.util.DateUtil.newDateTime;

@Component
public class OutboundMessageNotification implements IOutboundMessageNotification {

    private final Logger log = Logger.getLogger(OutboundMessageNotification.class);
    private EventRelay eventRelay;
    private final Integer maxRetries;

    @Autowired
    private AllOutboundSMS allOutboundSMS;

    private SettingsFacade settings;

    @Autowired
    public OutboundMessageNotification(EventRelay eventRelay, SettingsFacade settings) {
        this.settings = settings;
        this.eventRelay = eventRelay;
        this.maxRetries = Integer.parseInt(settings.getProperty(SmsProperties.MAX_RETRIES));
    }

    @Override
    public void process(AGateway gateway, OutboundMessage msg) {
        log.info(String.format("[%s] Outbound notification for (%s) with message (%s) received from gateway (%s)", msg.getMessageStatus().toString(), msg.getRecipient(), msg.getText(), gateway.getGatewayId()));

        DateTime sentTime = newDateTime(msg.getDate());
        if (sendingFailed(msg)) {
            if (msg.getRetryCount() >= maxRetries) {
                raiseFailureEvent(msg, sentTime);
            } else if (msg.getRetryCount() < maxRetries) {
                allOutboundSMS.createOrReplace(new OutboundSMS(msg.getRecipient(), msg.getRefNo(), msg.getText(), sentTime, KEEPTRYING));
            }
        } else {
            allOutboundSMS.createOrReplace(new OutboundSMS(msg.getRecipient(), msg.getRefNo(), msg.getText(), sentTime, INPROGRESS));
        }
    }

    private void raiseFailureEvent(OutboundMessage msg, DateTime sentTime) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(RECIPIENT, msg.getRecipient());
        parameters.put(MESSAGE, msg.getText());
        eventRelay.sendEventMessage(new MotechEvent(EventSubjects.SMS_FAILURE_NOTIFICATION, parameters));
        allOutboundSMS.createOrReplace(new OutboundSMS(msg.getRecipient(), msg.getRefNo(), msg.getText(), sentTime, ABORTED));
    }

    private boolean sendingFailed(OutboundMessage msg) {
        return msg.getMessageStatus().equals(OutboundMessage.MessageStatuses.FAILED);
    }
}
