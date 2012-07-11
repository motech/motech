package org.motechproject.server.verboice;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.server.verboice.domain.VerboiceHandler;
import org.motechproject.decisiontree.service.FlowSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

import static java.lang.String.format;

/**
 * Verboice specific implementation of the IVR Service interface
 */
@Component
@Qualifier("VerboiceIVRService")
public class VerboiceIVRService implements IVRService {

    private static Logger log = LoggerFactory.getLogger(VerboiceIVRService.class);

    private Properties verboiceProperties;
    private HttpClient commonsHttpClient;
    private FlowSessionService flowSessionService;
    @Autowired(required = false)
    private VerboiceHandler handler;

    @Autowired
    public VerboiceIVRService(@Qualifier("verboiceProperties") Properties verboiceProperties, HttpClient commonsHttpClient, FlowSessionService flowSessionService) {
        this.verboiceProperties = verboiceProperties;
        this.commonsHttpClient = commonsHttpClient;
        this.flowSessionService = flowSessionService;
        this.commonsHttpClient.getParams().setAuthenticationPreemptive(true);
        this.commonsHttpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(verboiceProperties.getProperty("username"), verboiceProperties.getProperty("password")));
    }

    public void setHandler(VerboiceHandler handler) {
        this.handler = handler;
    }

    @Override
    public void initiateCall(CallRequest callRequest) {
        initSession(callRequest);
        try {
            GetMethod getMethod = new GetMethod(outgoingCallUri(callRequest));
            int status = commonsHttpClient.executeMethod(getMethod);
            log.info(String.format("[%d]\n%s", status, getMethod.getResponseBodyAsString()));
        } catch (IOException e) {
            log.error("Exception when initiating call : ", e);
        }
    }

    private void initSession(CallRequest callRequest) {
        FlowSession flowSession = flowSessionService.getSession(callRequest.getCallId());
        for (String key : callRequest.getPayload().keySet()) {
            if (!key.equals("callback_url"))
                flowSession.set(key, callRequest.getPayload().get(key));
        }
        flowSessionService.updateSession(flowSession);
    }

    private String outgoingCallUri(CallRequest callRequest) {
        String callbackUrlParameter = "";
        if (callRequest.getPayload() != null && !callRequest.getPayload().isEmpty() && callRequest.getPayload().containsKey("callback_url")) {
            callbackUrlParameter = "&" + "callback_url" + "=" + callRequest.getPayload().get("callback_url");
        }
        return format(
                "http://%s:%s/api/call?motech_call_id=%s&channel=%s&address=%s%s",
                verboiceProperties.getProperty("host"),
                verboiceProperties.getProperty("port"),
                callRequest.getCallId(),
                callRequest.getCallBackUrl(),
                callRequest.getPhone(), callbackUrlParameter
        );
    }

    public VerboiceHandler getHandler() {
        return handler;
    }
}
