package org.motechproject.sms.smpp;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.commons.api.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
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
import static org.motechproject.sms.api.DeliveryStatus.RECEIVED;
import static org.motechproject.sms.api.DeliveryStatus.UNKNOWN;
import static org.motechproject.sms.api.SMSType.INBOUND;
import static org.motechproject.sms.api.constants.EventDataKeys.INBOUND_MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.SENDER;
import static org.motechproject.sms.api.constants.EventDataKeys.STATUS_MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.TIMESTAMP;
import static org.motechproject.sms.api.constants.EventSubjects.INBOUND_SMS;
import static org.motechproject.sms.api.constants.EventSubjects.SMS_DELIVERY_REPORT;

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
            relayEvent(data, INBOUND_SMS);
            smsAuditService.log(new SmsRecord(INBOUND, msg.getOriginator(), msg.getText(), newDateTime(msg.getDate()), RECEIVED, Integer.toString(msg.getMpRefNo())));

        } else if (msgType.equals(Message.MessageTypes.STATUSREPORT)) {
            if (!(msg instanceof StatusReportMessage)) {
                throw new MotechException("Unexpected message type: " + msg.getClass().getName());
            }
            StatusReportMessage statusMessage = (StatusReportMessage) msg;
            String statusName = statusMessage.getStatus().name();

            DeliveryStatus status;

            try {
                status = DeliveryStatus.valueOf(statusName);
            } catch (IllegalArgumentException e) {
                /* status not exists in DeliverStatus enum */
                status = UNKNOWN;
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put(SENDER, msg.getOriginator());
            data.put(STATUS_MESSAGE, statusMessage);
            data.put(TIMESTAMP, new DateTime(msg.getDate()));

            relayEvent(data, SMS_DELIVERY_REPORT);

            smsAuditService.updateDeliveryStatus(statusMessage.getRecipient(), statusMessage.getRefNo(), status.name());
        }
    }

    private void relayEvent(Map<String, Object> data, String subject) {
        eventRelay.sendEventMessage(new MotechEvent(subject, data));
    }
}
