package org.motechproject.sms.smpp;

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

	private Service service;
    private Properties properties;
    private static final String temporaryGroup = "temporary_group";

    @Autowired
	public ManagedSmslibService(Service smslibService, @Qualifier("smppProperties") Properties properties) {
		this.service = smslibService;
        this.properties = properties;
        registerGateway();
	}

    private void registerGateway() {
        JSMPPGateway jsmppGateway = new JSMPPGateway(GATEWAY_ID, property("host"), Integer.parseInt(property("port")), new BindAttributes(property("system_id"), property("password"), null, BindAttributes.BindType.TRANSCEIVER));
        try {
            service.addGateway(jsmppGateway);
        } catch (GatewayException e) {
            // This should never really happen as SmsLib service will always be in STOPPED state at this point.
        }
    }

    @PostConstruct
    public void connect() throws SMSLibException, IOException, InterruptedException {
        service.startService();
    }

    @PreDestroy
    public void disconnect() throws SMSLibException, IOException, InterruptedException {
        service.stopService();
    }

	public void queueMessage(List<String> recipients, final String message) throws GatewayException, IOException, TimeoutException, InterruptedException {
		service.createGroup(temporaryGroup);
		for (String recipient : recipients)
			service.addToGroup(temporaryGroup, recipient);

        service.queueMessage(new OutboundMessage() {{
            setRecipient(temporaryGroup);
            setText(message);
            setGatewayId(GATEWAY_ID);
        }});

        service.removeGroup(temporaryGroup);
	}

    private String property(String name) {
        return properties.get(name).toString();
    }
}
