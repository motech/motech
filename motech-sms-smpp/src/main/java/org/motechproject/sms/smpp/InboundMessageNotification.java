package org.motechproject.sms.smpp;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.InboundSMS;
import org.motechproject.sms.repository.AllInboundSMS;
import org.motechproject.sms.repository.AllOutboundSMS;
import org.motechproject.sms.smpp.constants.EventSubjects;
import org.smslib.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static org.motechproject.sms.smpp.constants.EventDataKeys.*;

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
            eventRelay.sendEventMessage(new MotechEvent(EventSubjects.INBOUND_SMS, data));
            allInboundSMS.createOrReplace(new InboundSMS(msg.getOriginator(), msg.getText(), msg.getDate(), msg.getUuid()));
        } else if (msgType.equals(Message.MessageTypes.STATUSREPORT)) {
            StatusReportMessage statusMessage = (StatusReportMessage) msg;
            allOutboundSMS.updateDeliveryStatus(statusMessage.getRecipient(), statusMessage.getRefNo(), statusMessage.getStatus().name());
        }

    }
}
