package org.motechproject.sms.smpp;

import org.motechproject.sms.smpp.constants.SmppProperties;
import org.motechproject.sms.smpp.constants.SmsProperties;
import org.smslib.*;
import org.smslib.smpp.BindAttributes;
import org.smslib.smpp.jsmpp.JSMPPGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Component
public class ManagedSmslibService {
	public static final String GATEWAY_ID = "smpp_gateway";
	private static final String temporaryGroup = "temporary_group";

	private Service smslibService;
	private Properties smsProperties;
	private Properties smppProperties;
	private OutboundMessageNotification outboundMessageNotification;
    private InboundMessageNotification inboundMessageNotification;

    @Autowired
	public ManagedSmslibService(Service smslibService, @Qualifier("smsProperties") Properties smsProperties, @Qualifier("smppProperties") Properties smppProperties, OutboundMessageNotification outboundMessageNotification, InboundMessageNotification inboundMessageNotification) {
		this.smslibService = smslibService;
		this.smsProperties = smsProperties;
		this.smppProperties = smppProperties;
		this.outboundMessageNotification = outboundMessageNotification;
        this.inboundMessageNotification = inboundMessageNotification;
        configureSmsLib();
		registerListeners();
		registerGateway();
	}

	private void registerListeners() {
		smslibService.setOutboundMessageNotification(outboundMessageNotification);
        smslibService.setInboundMessageNotification(inboundMessageNotification);
	}

	private void configureSmsLib() {
		String maxRetriesProperty = smsProperties.getProperty(SmsProperties.MAX_RETRIES);
		if (maxRetriesProperty != null)
			smslibService.getSettings().QUEUE_RETRIES = Integer.parseInt(maxRetriesProperty);
	}

	private void registerGateway() {
		JSMPPGateway jsmppGateway = new JSMPPGateway(GATEWAY_ID,
				smppProperties.getProperty(SmppProperties.HOST),
				Integer.parseInt(smppProperties.getProperty(SmppProperties.PORT)),
				new BindAttributes(smppProperties.getProperty(SmppProperties.SYSTEM_ID), smppProperties.getProperty(SmppProperties.PASSWORD), null, BindAttributes.BindType.TRANSCEIVER));
		try {
			smslibService.addGateway(jsmppGateway);
		} catch (GatewayException e) {
			// This should never really happen as SmsLib service will always be in STOPPED state at this point.
		}
	}

	@PostConstruct
	public void connect() throws SMSLibException, IOException, InterruptedException {
		smslibService.startService();
	}

	@PreDestroy
	public void disconnect() throws SMSLibException, IOException, InterruptedException {
		smslibService.stopService();
	}

	public void queueMessage(List<String> recipients, final String message) throws GatewayException, IOException, TimeoutException, InterruptedException {
		smslibService.createGroup(temporaryGroup);
		for (String recipient : recipients)
			smslibService.addToGroup(temporaryGroup, recipient);

		smslibService.queueMessage(new OutboundMessage() {{
			setRecipient(temporaryGroup);
			setText(message);
			setGatewayId(GATEWAY_ID);
		}});

		smslibService.removeGroup(temporaryGroup);
	}
}
