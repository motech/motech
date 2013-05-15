package org.motechproject.server.verboice;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.motechproject.callflow.domain.CallDetailRecord;
import org.motechproject.callflow.domain.CallDirection;
import org.motechproject.callflow.domain.FlowSessionRecord;
import org.motechproject.callflow.service.FlowSessionService;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.verboice.domain.VerboiceServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Verboice specific implementation of the IVR Service interface
 */
@Component
public class VerboiceIVRService implements IVRService {
    private static Logger log = LoggerFactory.getLogger(VerboiceIVRService.class);
    private static final String CALLBACK_URL = "callback_url";
    private static final String CALLBACK_STATUS_URL = "status_callback_url";
    private static final String CALL_FLOW_ID = "call_flow_id";

    private SettingsFacade settings;
    private HttpClient commonsHttpClient;
    private FlowSessionService flowSessionService;
    private Gson gsonParser;

    @Autowired
    public VerboiceIVRService(@Qualifier("verboiceAPISettings") SettingsFacade settings, HttpClient commonsHttpClient, FlowSessionService flowSessionService) {
        this.settings = settings;
        this.commonsHttpClient = commonsHttpClient;
        this.flowSessionService = flowSessionService;
        gsonParser = new Gson();
    }

    @Override
    public void initiateCall(CallRequest callRequest) {
        FlowSession session = initSession(callRequest);
        try {
            GetMethod getMethod = new GetMethod(outgoingCallUri(callRequest));
            getMethod.addRequestHeader("Authorization", "Basic " + basicAuthValue());
            int status = commonsHttpClient.executeMethod(getMethod);
            String responseBody = getMethod.getResponseBodyAsString();
            if (responseBody != null && responseBody.trim().length() > 0) {
                VerboiceServerResponse response = null;
                try {
                    response = gsonParser.fromJson(responseBody, VerboiceServerResponse.class);
                } catch (JsonParseException e) {
                    log.error("Unable to parse response from Verboice: " + responseBody);
                }
                if (response != null && response.getCallId() != null) {
                    flowSessionService.updateSessionId(session.getSessionId(), response.getCallId());
                }
            }
            log.info(String.format("[%d]\n%s", status, getMethod.getResponseBodyAsString()));
        } catch (IOException e) {
            log.error("Exception when initiating call: ", e);
        }
    }

    private String basicAuthValue() {
        return new String(Base64.encodeBase64((settings.getProperty("username") + ":" + settings.getProperty("password")).getBytes()));
    }

    private FlowSession initSession(CallRequest callRequest) {
        FlowSessionRecord flowSession = (FlowSessionRecord) flowSessionService.findOrCreate(callRequest.getCallId(), callRequest.getPhone());
        final CallDetailRecord callDetailRecord = flowSession.getCallDetailRecord();
        callDetailRecord.setCallDirection(CallDirection.Outbound);
        for (String key : callRequest.getPayload().keySet()) {
            if (!CALLBACK_URL.equals(key) && !CALLBACK_STATUS_URL.equals(key)) {
                flowSession.set(key, callRequest.getPayload().get(key));
            }
        }
        flowSessionService.updateSession(flowSession);

        return flowSession;
    }

    private String outgoingCallUri(CallRequest callRequest) {
        String callbackUrlParameter = "";
        String callbackStatusUrlParameter = "";
        String callFlowId = "";
        if (callRequest.getPayload() != null && callRequest.getPayload().containsKey(CALLBACK_URL)) {
            callbackUrlParameter = "&" + CALLBACK_URL + "=" + callRequest.getPayload().get(CALLBACK_URL);
        }
        if (callRequest.getPayload() != null && callRequest.getPayload().containsKey(CALLBACK_STATUS_URL)) {
            callbackStatusUrlParameter = "&" + CALLBACK_STATUS_URL + "=" + callRequest.getPayload().get(CALLBACK_STATUS_URL);
        }
        if (callRequest.getPayload() != null && callRequest.getPayload().containsKey(CALL_FLOW_ID)) {
            callFlowId = "&" + CALL_FLOW_ID + "=" + callRequest.getPayload().get(CALL_FLOW_ID);
        }

        return format(
                "http://%s:%s/api/call?motech_call_id=%s&channel=%s&address=%s%s%s%s",
                settings.getProperty("host"),
                settings.getProperty("port"),
                callRequest.getCallId(),
                isBlank(callRequest.getCallBackUrl())?settings.getProperty("channel"):callRequest.getCallBackUrl(),
                        callRequest.getPhone(), callbackUrlParameter, callbackStatusUrlParameter, callFlowId
                );
    }
}
