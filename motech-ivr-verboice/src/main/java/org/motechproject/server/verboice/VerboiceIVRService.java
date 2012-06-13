package org.motechproject.server.verboice;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.server.verboice.domain.VerboiceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
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
    private VerboiceHandler handler;

    @Autowired
    public VerboiceIVRService(@Qualifier("verboiceProperties") Properties verboiceProperties, HttpClient commonsHttpClient) {
        this.verboiceProperties = verboiceProperties;
        this.commonsHttpClient = commonsHttpClient;
        this.commonsHttpClient.getParams().setAuthenticationPreemptive(true);
        this.commonsHttpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(verboiceProperties.getProperty("username"), verboiceProperties.getProperty("password")));
    }

    public void registerHandler(VerboiceHandler handler) {
        this.handler = handler;
    }

    @Override
    public void initiateCall(CallRequest callRequest) {
        try {
            GetMethod getMethod = new GetMethod(outgoingCallUri(callRequest));
            int status = commonsHttpClient.executeMethod(getMethod);
            log.info(String.format("[%d]\n%s", status, getMethod.getResponseBodyAsString()));
        } catch (IOException e) {
            log.error("Exception when initiating call : ", e);
        }
    }

    private String outgoingCallUri(CallRequest callRequest) {
        String callbackUrlParameter = "";
        if (callRequest.getPayload() != null && !callRequest.getPayload().isEmpty() && callRequest.getPayload().containsKey("callback_url")) {
            callbackUrlParameter = "&" + "callback_url" + "=" + callRequest.getPayload().get("callback_url");
        }
        return format(
                "http://%s:%s/api/call?channel=%s&address=%s%s",
                verboiceProperties.getProperty("host"),
                verboiceProperties.getProperty("port"),
                callRequest.getCallBackUrl(),
                callRequest.getPhone(), callbackUrlParameter
        );
    }

    public VerboiceHandler getHandler() {
        return handler;
    }
}