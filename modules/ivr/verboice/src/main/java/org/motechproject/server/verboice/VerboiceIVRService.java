package org.motechproject.server.verboice;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.lang.String.format;

/**
 * Verboice specific implementation of the IVR Service interface
 */
@Component
public class VerboiceIVRService implements IVRService {
    private static Logger log = LoggerFactory.getLogger(VerboiceIVRService.class);
    private static final String CALLBACK_URL = "callback_url";
    private static final String CALLBACK_STATUS_URL = "status_callback_url";

    private SettingsFacade settings;
    private HttpClient commonsHttpClient;
    private FlowSessionService flowSessionService;

    @Autowired
    public VerboiceIVRService(@Qualifier("verboiceAPISettings") SettingsFacade settings, HttpClient commonsHttpClient, FlowSessionService flowSessionService) {
        this.settings = settings;
        this.commonsHttpClient = commonsHttpClient;
        this.flowSessionService = flowSessionService;
    }

    @Override
    public void initiateCall(CallRequest callRequest) {
        initSession(callRequest);
        try {
            GetMethod getMethod = new GetMethod(outgoingCallUri(callRequest));
            getMethod.addRequestHeader("Authorization", "Basic " + basicAuthValue());
            int status = commonsHttpClient.executeMethod(getMethod);
            log.info(String.format("[%d]\n%s", status, getMethod.getResponseBodyAsString()));
        } catch (IOException e) {
            log.error("Exception when initiating call: ", e);
        }
    }

    private String basicAuthValue() {
        return new String(Base64.encodeBase64((settings.getProperty("username") + ":" + settings.getProperty("password")).getBytes()));
    }

    private void initSession(CallRequest callRequest) {
        FlowSession flowSession = flowSessionService.findOrCreate(callRequest.getCallId(), callRequest.getPhone());
        for (String key : callRequest.getPayload().keySet()) {
            if (!CALLBACK_URL.equals(key)) {
                flowSession.set(key, callRequest.getPayload().get(key));
            }
        }
        flowSessionService.updateSession(flowSession);
    }

    private String outgoingCallUri(CallRequest callRequest) {
        String callbackUrlParameter = "";
        String callbackStatusUrlParameter = "";
        if (callRequest.getPayload() != null && !callRequest.getPayload().isEmpty() && callRequest.getPayload().containsKey(CALLBACK_URL)) {
            callbackUrlParameter = "&" + CALLBACK_URL + "=" + callRequest.getPayload().get(CALLBACK_URL);
        }
        if (callRequest.getPayload() != null && !callRequest.getPayload().isEmpty() && callRequest.getPayload().containsKey(CALLBACK_STATUS_URL)) {
            callbackStatusUrlParameter = "&" + CALLBACK_STATUS_URL + "=" + callRequest.getPayload().get(CALLBACK_STATUS_URL);
        }
        return format(
            "http://%s:%s/api/call?motech_call_id=%s&channel=%s&address=%s%s%s",
            settings.getProperty("host"),
            settings.getProperty("port"),
            callRequest.getCallId(),
            callRequest.getCallBackUrl(),
            callRequest.getPhone(), callbackUrlParameter, callbackStatusUrlParameter
        );
    }
}
