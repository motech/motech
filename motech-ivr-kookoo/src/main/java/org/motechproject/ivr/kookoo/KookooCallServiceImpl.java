package org.motechproject.ivr.kookoo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONObject;
import org.motechproject.server.service.ivr.CallRequest;
import org.motechproject.server.service.ivr.IVRService;
import org.motechproject.server.service.ivr.IVRSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Properties;

@Service
public class KookooCallServiceImpl implements IVRService {
    public static final String KOOKOO_OUTBOUND_URL = "kookoo.outbound.url";
    public static final String KOOKOO_API_KEY = "kookoo.api.key";


    @Autowired
    @Qualifier("ivrProperties")
    private Properties properties;
    private HttpClient httpClient = new HttpClient();

    public KookooCallServiceImpl() {
    }

    public KookooCallServiceImpl(Properties properties, HttpClient httpClient) {
        this.properties = properties;
        this.httpClient = httpClient;
    }

    public void dial(String phoneNumber, Map<String, String> params, String callBackUrl) {
        try {
            JSONObject json = new JSONObject(params);
            String applicationUrl = callBackUrl + "?tamaData=" + json.toString();
            applicationUrl = URLEncoder.encode(applicationUrl, "UTF-8");

            GetMethod getMethod = new GetMethod(properties.get(KOOKOO_OUTBOUND_URL).toString());
            getMethod.setQueryString(new NameValuePair[]{
                    new NameValuePair("api_key", properties.get(KOOKOO_API_KEY).toString()),
                    new NameValuePair("url", applicationUrl),
                    new NameValuePair("phone_no", phoneNumber)
            });
            httpClient.executeMethod(getMethod);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initiateCall(CallRequest callRequest) {
        if (callRequest == null) throw new IllegalArgumentException("Missing call request");
        callRequest.getPayload().put(IVRSession.IVRCallAttribute.IS_OUTBOUND_CALL, "true");
        dial(callRequest.getPhone(), callRequest.getPayload(), callRequest.getCallBackUrl());
    }
}
