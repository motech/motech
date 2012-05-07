package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.motechproject.sms.smpp.constants.SmsProperties;
import org.smslib.GatewayException;
import org.smslib.OutboundMessage;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.smpp.jsmpp.JSMPPGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.motechproject.sms.smpp.constants.SmppProperties.*;

@Component
public class ManagedSmslibService {
    private static final String GATEWAY_ID = "smpp_gateway";
    private static final String QUEUE_PERSISTENCE_PATH = ".";

    @Qualifier("smsProperties")
    private Properties smsProperties;
    @Qualifier("smppProperties")
    private Properties smppProperties;

    private Service smslibService;
    private OutboundMessageNotification outboundMessageNotification;
    private InboundMessageNotification inboundMessageNotification;
    private JSMPPPropertiesMapper jsmppMapper;

    @Autowired
    public ManagedSmslibService(Service smslibService, Properties smsProperties, Properties smppProperties, OutboundMessageNotification outboundMessageNotification, InboundMessageNotification inboundMessageNotification) {
        this.smslibService = smslibService;
        this.smsProperties = smsProperties;
        this.smppProperties = smppProperties;
        this.outboundMessageNotification = outboundMessageNotification;
        this.inboundMessageNotification = inboundMessageNotification;
        this.jsmppMapper = new JSMPPPropertiesMapper(smppProperties);
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
                jsmppMapper.getHost(),
                jsmppMapper.getPort(),
                jsmppMapper.getBindAttributes());

        jsmppGateway.setSourceAddress(jsmppMapper.getSourceAddress());
        jsmppGateway.setDestinationAddress(jsmppMapper.getDestinationAddress());
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
        for (String recipient : recipients) {
            OutboundMessage outboundMessage = getOutboundMessage(message, recipient);
            smslibService.queueMessage(outboundMessage);
        }
    }

    public void queueMessageAt(List<String> recipients, final String message, DateTime dateTime) {
        for (String recipient : recipients) {
            OutboundMessage outboundMessage = getOutboundMessage(message, recipient);
            smslibService.queueMessageAt(outboundMessage, dateTime.toDate());
        }
    }

    private OutboundMessage getOutboundMessage(String message, String recipient) {
        OutboundMessage outboundMessage = new OutboundMessage();
        outboundMessage.setRecipient(recipient);
        outboundMessage.setStatusReport(Boolean.valueOf(smppProperties.getProperty(DELIVERY_REPORTS)));
        outboundMessage.setText(message);
        outboundMessage.setGatewayId(GATEWAY_ID);
        return outboundMessage;
    }
}
