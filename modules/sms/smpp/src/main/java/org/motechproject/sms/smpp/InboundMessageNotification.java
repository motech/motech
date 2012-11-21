package org.motechproject.sms.smpp;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.sms.smpp.constants.EventSubjects;
import org.motechproject.sms.smpp.repository.AllInboundSMS;
import org.motechproject.sms.smpp.repository.AllOutboundSMS;
import org.smslib.AGateway;
import org.smslib.IInboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.Message;
import org.smslib.StatusReportMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.sms.api.constants.EventDataKeys.INBOUND_MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.SENDER;
import static org.motechproject.sms.api.constants.EventDataKeys.TIMESTAMP;
import static org.motechproject.sms.smpp.constants.EventDataKeys.STATUS_MESSAGE;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;

@Component
public class InboundMessageNotification implements IInboundMessageNotification {
    private final Logger log = Logger.getLogger(InboundMessageNotification.class);
    private EventRelay eventRelay;
    private AllInboundSMS allInboundSMS;
    private AllOutboundSMS allOutboundSMS;

    @Autowired
    public InboundMessageNotification(EventRelay eventRelay, AllInboundSMS allInboundSMS, AllOutboundSMS allOutboundSMS) {
        this.eventRelay = eventRelay;
        this.allInboundSMS = allInboundSMS;
        this.allOutboundSMS = allOutboundSMS;
    }

    @Override
    public void process(AGateway gateway, Message.MessageTypes msgType, InboundMessage msg) {
        log.info(String.format("Inbound notification from (%s) with message (%s) of type (%s) received from gateway (%s)", msg.getOriginator(), msg.getText(), msgType.toString(), gateway.getGatewayId()));

        if (msgType.equals(Message.MessageTypes.INBOUND)) {
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put(SENDER, msg.getOriginator());
            data.put(INBOUND_MESSAGE, msg.getText());
            data.put(TIMESTAMP, new DateTime(msg.getDate()));
            relayEvent(data, EventSubjects.INBOUND_SMS);
            allInboundSMS.add(new InboundSMS(msg.getOriginator(), msg.getText(), newDateTime(msg.getDate())));
        } else if (msgType.equals(Message.MessageTypes.STATUSREPORT)) {
            StatusReportMessage statusMessage = (StatusReportMessage) msg;
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put(SENDER, msg.getOriginator());
            data.put(STATUS_MESSAGE, statusMessage);
            data.put(TIMESTAMP, new DateTime(msg.getDate()));
            relayEvent(data, EventSubjects.SMS_DELIVERY_REPORT);
            allOutboundSMS.updateDeliveryStatus(statusMessage.getRecipient(), statusMessage.getRefNo(), statusMessage.getStatus().name());
        }
    }

    private void relayEvent(Map<String, Object> data, String subject) {
        eventRelay.sendEventMessage(new MotechEvent(subject, data));
    }
}
