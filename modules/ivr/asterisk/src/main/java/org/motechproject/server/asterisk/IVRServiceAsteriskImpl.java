package org.motechproject.server.asterisk;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.DefaultAsteriskServer;
import org.asteriskjava.live.ManagerCommunicationException;
import org.asteriskjava.live.NoSuchChannelException;
import org.asteriskjava.live.OriginateCallback;
import org.motechproject.ivr.model.CallInitiationException;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.server.asterisk.callback.MotechAsteriskCallBackImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Asterisk specific implementation of the IVR Service interface
 */
public class IVRServiceAsteriskImpl implements IVRService {
    public static final String VXML_URL = "vxml.url";
    public static final String VXML_TIMEOUT = "vxml.timeout";

    private final String asteriskApplication = "Agi";

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final URLCodec urlCodec = new URLCodec();

    private AsteriskServer asteriskServer;
    private String agiUrl;
    private long timeout;
    private String vxmlBaseURL;


    public IVRServiceAsteriskImpl(String asteriskServerHost, String asteriskUserName, String asteriskUserPassword) {
        asteriskServer = new DefaultAsteriskServer(asteriskServerHost, asteriskUserName, asteriskUserPassword);
    }

    public IVRServiceAsteriskImpl(String asteriskServerHost, int asteriskServerPort, String asteriskUserName, String asteriskUserPassword) {
        asteriskServer = new DefaultAsteriskServer(asteriskServerHost, asteriskServerPort, asteriskUserName, asteriskUserPassword);
    }

    @Override
    public void initiateCall(CallRequest callRequest) {

        if (callRequest == null) {
            throw new IllegalArgumentException("CallRequest can not be null");
        }

        OriginateCallback asteriskCallBack = new MotechAsteriskCallBackImpl(callRequest);
        try {

            String destinationPhone = callRequest.getPhone();

            String encodedVxmlUrl;
            try {
                encodedVxmlUrl = urlCodec.encode(getVxmlUrl());
            } catch (EncoderException e) {
                String errorMessage = "Invalid Voice XML URL: " + getVxmlUrl();
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage, e);
            }

            String data = agiUrl + encodedVxmlUrl;

            log.info("Initiating call to: " + destinationPhone + " VXML URL: " + data);

            asteriskServer.originateToApplicationAsync(destinationPhone, asteriskApplication,
                    data, getTimeOut(), asteriskCallBack);
        } catch (ManagerCommunicationException e) {
            String errorMessage = "Can not initiate call: " + e.getMessage();
            throw new CallInitiationException(errorMessage, e);
        } catch (NoSuchChannelException e) {
            String errorMessage = "Can not initiate call: " + e.getMessage();
            throw new CallInitiationException(errorMessage, e); //TODO - check what actually causes that exception
        }
    }

    private String getVxmlUrl() {
        //TODO: construct the vxml url.
        return vxmlBaseURL;
    }

    private long getTimeOut() {
        return timeout;
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

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setVxmlBaseURL(String vxmlBaseURL) {
        this.vxmlBaseURL = vxmlBaseURL;
    }
}
