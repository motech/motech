package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.smpp.constants.SmsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.motechproject.sms.smpp.constants.SmppProperties.DELIVERY_REPORTS;

@Component
public class ManagedSmslibService {
    private final Logger log = LoggerFactory.getLogger(ManagedSmslibService.class);
    private static final String GATEWAY_ID = "smpp_gateway";
    private static final String QUEUE_PERSISTENCE_PATH = ".";

    private SettingsFacade smsSettings;
    private SettingsFacade smppSettings;

    private Service smslibService;
    private OutboundMessageNotification outboundMessageNotification;
    private InboundMessageNotification inboundMessageNotification;
    private JSMPPPropertiesMapper jsmppMapper;

    @Autowired
    public ManagedSmslibService(Service smslibService, OutboundMessageNotification outboundMessageNotification,
                                InboundMessageNotification inboundMessageNotification, @Qualifier("smsApiSettings") SettingsFacade smsSettings, @Qualifier("smsSmppSettings") SettingsFacade smppSettings) {
        this.smslibService = smslibService;
        this.outboundMessageNotification = outboundMessageNotification;
        this.inboundMessageNotification = inboundMessageNotification;
        this.smsSettings = smsSettings;
        this.jsmppMapper = new JSMPPPropertiesMapper(smppSettings.asProperties());
        this.smppSettings = smppSettings;

        configureSmsLib();
        registerGateway();
        registerListeners();
    }

    private void registerListeners() {
        log.info("Register listeners");

        smslibService.setOutboundMessageNotification(outboundMessageNotification);
        smslibService.setInboundMessageNotification(inboundMessageNotification);
    }

    private void configureSmsLib() {
        log.info("Configure SMS Lib Service");

        String maxRetriesProperty = smsSettings.getProperty(SmsProperties.MAX_RETRIES);

        if (maxRetriesProperty != null) {
            smslibService.getSettings().QUEUE_RETRIES = Integer.parseInt(maxRetriesProperty);
        }

        smslibService.getSettings().QUEUE_DIRECTORY = QUEUE_PERSISTENCE_PATH;
    }

    private void registerGateway() {
        log.info("Register JSMPP gateway");
        try {
            JSMPPGateway jsmppGateway = new JSMPPGateway(GATEWAY_ID,
                jsmppMapper.getHost(),
                jsmppMapper.getPort(),
                jsmppMapper.getBindAttributes());

            jsmppGateway.setSourceAddress(jsmppMapper.getSourceAddress());
            jsmppGateway.setDestinationAddress(jsmppMapper.getDestinationAddress());

            smslibService.addGateway(jsmppGateway);
        } catch (Exception e) {
            // This should never really happen as SmsLib service will always be in STOPPED state at this point.
            log.warn("Smpp gateway connection failed", e);
        }
    }

    @PostConstruct
    public void connect() throws SMSLibException, IOException, InterruptedException {
        log.info("Start SMS Lib Service");

        try {
            smslibService.startService();
        } catch (SMSLibException | IOException | InterruptedException e) {
            log.error("Error: ", e);
        }
    }

    @PreDestroy
    public void disconnect() throws SMSLibException, IOException, InterruptedException {
        log.info("Stop SMS Lib Service");

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
        outboundMessage.setStatusReport(Boolean.valueOf(smppSettings.getProperty(DELIVERY_REPORTS)));
        outboundMessage.setText(message);
        outboundMessage.setGatewayId(GATEWAY_ID);
        return outboundMessage;
    }
}
