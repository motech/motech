package org.motechproject.sms.smpp;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.InboundSMS;
import org.motechproject.sms.repository.AllInboundSMS;
import org.motechproject.sms.repository.AllOutboundSMS;
import org.motechproject.sms.smpp.constants.EventSubjects;
import org.motechproject.util.DateUtil;
import org.smslib.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static org.motechproject.sms.smpp.constants.EventDataKeys.*;
import static org.motechproject.util.DateUtil.newDateTime;

@Component
public class InboundMessageNotification implements IInboundMessageNotification {
    private static final Logger log = Logger.getLogger(InboundMessageNotification.class);
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
            DateTime receivedDate = newDateTime(msg.getDate());
            allInboundSMS.add(new InboundSMS(msg.getOriginator(), msg.getText(), receivedDate.toLocalDate(), DateUtil.time(receivedDate)));
        } else if (msgType.equals(Message.MessageTypes.STATUSREPORT)) {
            StatusReportMessage statusMessage = (StatusReportMessage) msg;
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put(SENDER, msg.getOriginator());
            data.put(STATUS_MESSAGE, statusMessage);
            data.put(TIMESTAMP, new DateTime(msg.getDate()));
            relayEvent(data, EventSubjects.SMS_DELIVERY_REPORT);
            allOutboundSMS.updateDeliveryStatus(statusMessage.getRecipient(), statusMessage.getRefNo(), newDateTime(statusMessage.getSent()), statusMessage.getStatus().name());
        }
    }

    private void relayEvent(HashMap<String, Object> data, String subject) {
        eventRelay.sendEventMessage(new MotechEvent(subject, data));
    }
}
