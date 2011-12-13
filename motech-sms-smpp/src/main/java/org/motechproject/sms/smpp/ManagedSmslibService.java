package org.motechproject.sms.smpp;

import org.smslib.*;
import org.smslib.smpp.BindAttributes;
import org.smslib.smpp.jsmpp.JSMPPGateway;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

public class ManagedSmslibService {
	private final String host = "localhost";
	private final int port = 2715;
	private final String systemId = "smppclient1";
	private final String password = "password";

	private Service service;

	public ManagedSmslibService(Service smslibService) {
		service = smslibService;
		addGateway();
	}

	public void sendMessage(List<String> recipients, String message) throws GatewayException, IOException, TimeoutException, InterruptedException {
		String groupName = "groupSms";
		service.createGroup(groupName);

		for (String recipient : recipients)
			service.addToGroup(groupName, recipient);

		OutboundMessage outboundMessage = new OutboundMessage();
		outboundMessage.setRecipient(groupName);
		outboundMessage.setText(message);
		service.sendMessage(outboundMessage);
		service.removeGroup(groupName);
	}

	@PostConstruct
	public void connect() throws SMSLibException, IOException, InterruptedException {
		service.startService();
	}

	private void addGateway() {
		JSMPPGateway jsmppGateway = new JSMPPGateway("smppcon", host, port, new BindAttributes(systemId, password, null, BindAttributes.BindType.TRANSCEIVER));
		try {
			service.addGateway(jsmppGateway);
		} catch (GatewayException e) {
			// This should never really happen as SmsLib service will always be in STOPPED state at this point.
		}
	}
}
