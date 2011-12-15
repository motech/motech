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
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String SYSTEM_ID = "system_id";
    public static final String PASSWORD = "password";
    public static final String MAX_RETRIES = "max_retries";
    public static final String RETRY_INTERVAL_SECS = "retry_interval_secs";

    private Service smslibService;
    private Properties smsProperties;
    private Properties smppProperties;
    private static final String temporaryGroup = "temporary_group";

    @Autowired
    public ManagedSmslibService(Service smslibService, @Qualifier("smsProperties") Properties smsProperties, @Qualifier("smppProperties") Properties smppProperties) {
        this.smslibService = smslibService;
        this.smsProperties = smsProperties;
        this.smppProperties = smppProperties;
        configureSmsLib();
        registerGateway();
    }

    private void configureSmsLib() {
        String maxRetriesProperty = smsProperties.getProperty(MAX_RETRIES);
        if (maxRetriesProperty != null)
            smslibService.getSettings().OUTBOUND_RETRIES = Integer.parseInt(maxRetriesProperty);
        String retryIntervalSecs = smsProperties.getProperty(RETRY_INTERVAL_SECS);
        if (retryIntervalSecs != null)
            smslibService.getSettings().OUTBOUND_RETRY_WAIT = Integer.parseInt(retryIntervalSecs) * 1000;
    }

    private void registerGateway() {
        JSMPPGateway jsmppGateway = new JSMPPGateway(GATEWAY_ID,
                smppProperties.getProperty(HOST),
                Integer.parseInt(smppProperties.getProperty(PORT)),
                new BindAttributes(smppProperties.getProperty(SYSTEM_ID), smppProperties.getProperty(PASSWORD), null, BindAttributes.BindType.TRANSCEIVER));
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
            //setGatewayId(GATEWAY_ID);
        }});

        smslibService.removeGroup(temporaryGroup);
    }
}
