package org.motechproject.server.kookoo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONObject;
import org.motechproject.commons.api.MotechException;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.Logger;

import static java.lang.String.format;

@Service("ivrServiceKookoo")
public class KookooCallServiceImpl implements IVRService {

    public static final String OUTBOUND_URL = "kookoo.outbound.url";
    public static final String API_KEY = "kookoo.api.key";
    public static final String API_KEY_KEY = "api_key";
    public static final String URL_KEY = "url";
    public static final String MOTECH_CALL_ID_KEY = "motech_call_id";
    public static final String PHONE_NUMBER_KEY = "phone_no";
    public static final String CUSTOM_DATA_KEY = "dataMap";
    public static final String IS_OUTBOUND_CALL = "is_outbound_call";
    private static final String CALLBACK_URL_KEY = "callback_url";

    private SettingsFacade settings;
    private HttpClient commonsHttpClient;

    private FlowSessionService flowSessionService;
    private Logger log = Logger.getLogger(this.getClass().getName());

    @Autowired
    public KookooCallServiceImpl(@Qualifier("kookooAPISettings") SettingsFacade settings, HttpClient commonsHttpClient, FlowSessionService flowSessionService) {
        this.settings = settings;
        this.commonsHttpClient = commonsHttpClient;
        this.flowSessionService = flowSessionService;
    }

    @Override
    public void initiateCall(CallRequest callRequest) {
        if (callRequest == null) {
            throw new IllegalArgumentException("Missing call request");
        }
        GetMethod getMethod = null;
        initSession(callRequest);
        try {
            callRequest.getPayload().put(IS_OUTBOUND_CALL, "true");
            JSONObject json = new JSONObject(callRequest.getPayload());

            String applicationReplyUrl = format("%s?%s=%s",
                callRequest.getCallBackUrl(), CUSTOM_DATA_KEY, json.toString());

            String statusCallbackUrl = format("%s?%s=%s",
                    callRequest.getStatusCallbackUrl(), MOTECH_CALL_ID_KEY, callRequest.getCallId());

            getMethod = new GetMethod(settings.getProperty(OUTBOUND_URL));
            getMethod.setQueryString(new NameValuePair[]{
                new NameValuePair(API_KEY_KEY, settings.getProperty(API_KEY)),
                new NameValuePair(PHONE_NUMBER_KEY, callRequest.getPhone()),
                new NameValuePair(MOTECH_CALL_ID_KEY, callRequest.getCallId()),
                new NameValuePair(CALLBACK_URL_KEY, statusCallbackUrl),
                new NameValuePair(URL_KEY, applicationReplyUrl)
            });
            log.info(String.format("Dialing %s", getMethod.getURI()));
            commonsHttpClient.executeMethod(getMethod);
        } catch (IOException e) {
            throw new MotechException("Error initiating call", e);
        } finally {
            if(getMethod != null ) {
                getMethod.releaseConnection();
            }
        }
    }

    private void initSession(CallRequest callRequest) {
        FlowSession flowSession = flowSessionService.findOrCreate(callRequest.getCallId(), callRequest.getPhone());
        for (String key : callRequest.getPayload().keySet()) {
            if (!"callback_url".equals(key)) {
                flowSession.set(key, callRequest.getPayload().get(key));
            }
        }
        flowSessionService.updateSession(flowSession);
    }
}
