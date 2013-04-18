package org.motechproject.sms.smpp;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.commons.api.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.sms.api.SMSType;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.motechproject.sms.smpp.constants.EventSubjects;
import org.smslib.AGateway;
import org.smslib.IInboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.Message;
import org.smslib.StatusReportMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.sms.api.constants.EventDataKeys.INBOUND_MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.SENDER;
import static org.motechproject.sms.api.constants.EventDataKeys.TIMESTAMP;
import static org.motechproject.sms.smpp.constants.EventDataKeys.STATUS_MESSAGE;

@Component
public class InboundMessageNotification implements IInboundMessageNotification {
    private final Logger log = Logger.getLogger(InboundMessageNotification.class);
    private EventRelay eventRelay;
    private SmsAuditService smsAuditService;

    @Autowired
    public InboundMessageNotification(EventRelay eventRelay, SmsAuditService smsAuditService) {
        this.eventRelay = eventRelay;
        this.smsAuditService = smsAuditService;
    }

    @Override
    public void process(AGateway gateway, Message.MessageTypes msgType, InboundMessage msg) {
        log.info(String.format("Inbound notification from (%s) with message (%s) of type (%s) received from gateway (%s)", msg.getOriginator(), msg.getText(), msgType.toString(), gateway.getGatewayId()));

        if (msgType.equals(Message.MessageTypes.INBOUND)) {
            HashMap<String, Object> data = new HashMap<>();
            data.put(SENDER, msg.getOriginator());
            data.put(INBOUND_MESSAGE, msg.getText());
            data.put(TIMESTAMP, new DateTime(msg.getDate()));
            relayEvent(data, EventSubjects.INBOUND_SMS);
            // TODO: Why delivery status and reference number are not set?
            smsAuditService.log(new SmsRecord(SMSType.INBOUND, msg.getOriginator(), msg.getText(), newDateTime(msg.getDate()), null, null));

        } else if (msgType.equals(Message.MessageTypes.STATUSREPORT)) {
            if (!(msg instanceof StatusReportMessage)) {
                throw new MotechException("Unexpected message type: " + msg.getClass().getName());
            }
            StatusReportMessage statusMessage = (StatusReportMessage) msg;
            HashMap<String, Object> data = new HashMap<>();
            data.put(SENDER, msg.getOriginator());
            data.put(STATUS_MESSAGE, statusMessage);
            data.put(TIMESTAMP, new DateTime(msg.getDate()));
            relayEvent(data, EventSubjects.SMS_DELIVERY_REPORT);
            //TODO: Check status exists in DeliverStatus enum
            smsAuditService.updateDeliveryStatus(statusMessage.getRecipient(), statusMessage.getRefNo(), statusMessage.getStatus().name());
        }
    }

    private void relayEvent(Map<String, Object> data, String subject) {
        eventRelay.sendEventMessage(new MotechEvent(subject, data));
    }
}
