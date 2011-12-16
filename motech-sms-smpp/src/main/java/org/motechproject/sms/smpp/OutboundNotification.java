package org.motechproject.sms.smpp;

import org.apache.log4j.Logger;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.smslib.AGateway;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class OutboundNotification implements IOutboundMessageNotification {
	private static final Logger log = Logger.getLogger(OutboundNotification.class);
	private OutboundEventGateway outboundEventGateway;

	@Autowired
	public OutboundNotification(OutboundEventGateway outboundEventGateway) {
		this.outboundEventGateway = outboundEventGateway;
	}

	@Override
	public void process(AGateway gateway, OutboundMessage msg) {
		log.info("Outbound notification received from gateway: " + gateway.getGatewayId());

		if (msg.getMessageStatus().equals(OutboundMessage.MessageStatuses.FAILED)) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(EventKeys.RECIPIENT, msg.getRecipient());
			parameters.put(EventKeys.MESSAGE, msg.getText());
			outboundEventGateway.sendEventMessage(new MotechEvent(EventSubject.SMS_FAILURE_NOTIFICATION, parameters));
		}
	}
}
