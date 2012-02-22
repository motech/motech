package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
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
    private static final String GATEWAY_ID = "smpp_gateway";
    private static final String TEMPORARY_GROUP = "temporary_group";
    private static final String QUEUE_PERSISTENCE_PATH = ".";

    @Qualifier("smsProperties")
    private Properties smsProperties;
    @Qualifier("smppProperties")
    private Properties smppProperties;

    private Service smslibService;
    private OutboundMessageNotification outboundMessageNotification;
    private InboundMessageNotification inboundMessageNotification;

    @Autowired
    public ManagedSmslibService(Service smslibService, Properties smsProperties, Properties smppProperties, OutboundMessageNotification outboundMessageNotification, InboundMessageNotification inboundMessageNotification) {
        this.smslibService = smslibService;
        this.smsProperties = smsProperties;
        this.smppProperties = smppProperties;
        this.outboundMessageNotification = outboundMessageNotification;
        this.inboundMessageNotification = inboundMessageNotification;
        configureSmsLib();
        registerGateway();
        registerListeners();
    }

    private void registerListeners() {
        smslibService.setOutboundMessageNotification(outboundMessageNotification);
        smslibService.setInboundMessageNotification(inboundMessageNotification);
    }

    private void configureSmsLib() {
        String maxRetriesProperty = smsProperties.getProperty(SmsProperties.MAX_RETRIES);
        if (maxRetriesProperty != null)
            smslibService.getSettings().QUEUE_RETRIES = Integer.parseInt(maxRetriesProperty);
        smslibService.getSettings().QUEUE_DIRECTORY = QUEUE_PERSISTENCE_PATH;
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

    public void queueMessage(List<String> recipients, final String message) {
        createGroupOfRecipients(recipients);

        OutboundMessage outboundMessage = getOutboundMessage(message);

        smslibService.queueMessage(outboundMessage);
        smslibService.removeGroup(TEMPORARY_GROUP);
    }

    public void queueMessageAt(List<String> recipients, final String message, DateTime dateTime) throws GatewayException, IOException, TimeoutException, InterruptedException {
        createGroupOfRecipients(recipients);

        OutboundMessage outboundMessage = getOutboundMessage(message);

        smslibService.queueMessageAt(outboundMessage, dateTime.toDate());
        smslibService.removeGroup(TEMPORARY_GROUP);
    }

    private OutboundMessage getOutboundMessage(String message) {
        OutboundMessage outboundMessage = new OutboundMessage();
        outboundMessage.setRecipient(TEMPORARY_GROUP);
        outboundMessage.setText(message);
        outboundMessage.setGatewayId(GATEWAY_ID);
        return outboundMessage;
    }

    private void createGroupOfRecipients(List<String> recipients) {
        smslibService.createGroup(TEMPORARY_GROUP);
        for (String recipient : recipients)
            smslibService.addToGroup(TEMPORARY_GROUP, recipient);
    }
}
