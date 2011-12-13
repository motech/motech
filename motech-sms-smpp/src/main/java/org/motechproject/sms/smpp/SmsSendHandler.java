package org.motechproject.sms.smpp;

import org.motechproject.model.MotechEvent;
import org.motechproject.sms.api.SmsEventHandler;
import org.smslib.Service;
import org.smslib.smpp.BindAttributes;
import org.smslib.smpp.jsmpp.JSMPPGateway;

public class SmsSendHandler implements SmsEventHandler {

	private final String host = "localhost";
	private final int port = 2715;
	private final String systemId = "smppclient1";
	private final String password = "password";

	@Override
	public void handle(MotechEvent event) throws Exception {
		JSMPPGateway jsmppGateway = new JSMPPGateway("smppcon", host, port, new BindAttributes(systemId, password, null, BindAttributes.BindType.TRANSCEIVER));
		Service service = Service.getInstance();
		service.addGateway(jsmppGateway);
		
		service.startService();
		

		service.stopService();
	}
}