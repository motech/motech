package org.motechproject.server.service.ivr.astersik;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.asteriskjava.live.*;
import org.motechproject.model.InitiateCallData;
import org.motechproject.server.service.ivr.CallInitiationException;
import org.motechproject.server.service.ivr.IVRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Asterisk specific implementation of the IVR Service interface
 *
 * Date: 07/03/11
 *
 */
public class IVRServiceAsteriskImpl implements IVRService {

    private final String asteriksApplication = "Agi";

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final URLCodec urlCodec = new URLCodec();

    private AsteriskServer asteriskServer;
    private String agiUrl;


    public IVRServiceAsteriskImpl(String asteriskServerHost, String asteriskUserName, String asteriskUserPassword) {
        asteriskServer = new DefaultAsteriskServer(asteriskServerHost, asteriskUserName, asteriskUserPassword);
    }

    public IVRServiceAsteriskImpl(String asteriskServerHost, int asteriskServerPort, String asteriskUserName, String asteriskUserPassword) {
        asteriskServer = new DefaultAsteriskServer(asteriskServerHost, asteriskServerPort, asteriskUserName, asteriskUserPassword);
    }

    @Override
    public void initiateCall(InitiateCallData initiateCallData) {

        if (initiateCallData ==null ) {

            throw new IllegalArgumentException("InitiateCallData can not be null");
        }

        OriginateCallback asteriskCallBack = new MotechAsteriskCallBackImpl();
        try {

            String destinationPhone = initiateCallData.getPhone();

            String encodedVxmlUrl;
            try {
            encodedVxmlUrl = urlCodec.encode(initiateCallData.getVxmlUrl());
        } catch (EncoderException e) {
            String errorMessage = "Invalid Voice XML URL: " + initiateCallData.getVxmlUrl();
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

            String data = agiUrl + encodedVxmlUrl;

            log.info("Initiating call to: " + destinationPhone);

             asteriskServer.originateToApplicationAsync(destinationPhone, asteriksApplication,
                     data, initiateCallData.getTimeOut(), asteriskCallBack);
        } catch (ManagerCommunicationException e) {
            String errorMessage = "Can not initiate call: " + e.getMessage();
            throw new CallInitiationException(errorMessage, e);
        } catch (NoSuchChannelException e) {
            String errorMessage = "Can not initiate call: " + e.getMessage();
            throw new CallInitiationException(errorMessage, e); //TODO - check what actually causes that exception
        }

    }

    public void setAgiUrl(String agiUrl) {
        this.agiUrl = agiUrl;
    }

    /**
     * This method is for Unit Test support only
     */
     void setAsteriskServer(AsteriskServer asteriskServer) {
        this.asteriskServer = asteriskServer;
    }

    AsteriskServer getAsteriskServer() {
        return asteriskServer;
    }
}
