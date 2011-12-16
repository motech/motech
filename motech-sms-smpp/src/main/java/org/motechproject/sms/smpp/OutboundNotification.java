package org.motechproject.sms.smpp;

import org.apache.log4j.Logger;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.smpp.constants.EventKeys;
import org.motechproject.sms.smpp.constants.EventSubject;
import org.motechproject.sms.smpp.constants.SmsProperties;
import org.smslib.AGateway;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Properties;

@Component
public class OutboundNotification implements IOutboundMessageNotification {
	private static final Logger log = Logger.getLogger(OutboundNotification.class);
	private OutboundEventGateway outboundEventGateway;
	private final Integer maxRetries;

	@Autowired
	public OutboundNotification(OutboundEventGateway outboundEventGateway, @Qualifier("smsProperties") Properties smsProperties) {
		this.outboundEventGateway = outboundEventGateway;
		this.maxRetries = Integer.parseInt(smsProperties.getProperty(SmsProperties.MAX_RETRIES));
	}

	@Override
	public void process(AGateway gateway, OutboundMessage msg) {
		log.info(String.format("[%s] Outbound notification for (%s) with message (%s) received from gateway (%s)", msg.getMessageStatus().toString(), msg.getRecipient(), msg.getText(), gateway.getGatewayId()));

		if (msg.getRetryCount() >= maxRetries && msg.getMessageStatus().equals(OutboundMessage.MessageStatuses.FAILED)) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(EventKeys.RECIPIENT, msg.getRecipient());
			parameters.put(EventKeys.MESSAGE, msg.getText());
			outboundEventGateway.sendEventMessage(new MotechEvent(EventSubject.SMS_FAILURE_NOTIFICATION, parameters));
		}
	}
}
