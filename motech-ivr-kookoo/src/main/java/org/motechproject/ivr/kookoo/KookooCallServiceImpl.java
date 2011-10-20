package org.motechproject.ivr.kookoo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONObject;
import org.motechproject.server.service.ivr.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.logging.Logger;

@Service
public class KookooCallServiceImpl implements IVRService {
    public static final String KOOKOO_OUTBOUND_URL = "kookoo.outbound.url";
    public static final String KOOKOO_API_KEY = "kookoo.api.key";
    public static final String IS_OUTBOUND_CALL = "is_outbound_call";

    @Autowired
    @Qualifier("ivrProperties")
    private Properties properties;

    private HttpClient httpClient = new HttpClient();
    private Logger log = Logger.getLogger(KookooCallServiceImpl.class.getName());

    public KookooCallServiceImpl() {
    }

    public KookooCallServiceImpl(Properties properties, HttpClient httpClient) {
        this.properties = properties;
        this.httpClient = httpClient;
    }

    @Override
    public void initiateCall(CallRequest callRequest) {
        if (callRequest == null) throw new IllegalArgumentException("Missing call request");

        try {
            callRequest.getPayload().put(IS_OUTBOUND_CALL, "true");
            JSONObject json = new JSONObject(callRequest.getPayload());
            String applicationUrl = callRequest.getCallBackUrl() + "?dataMap=" + json.toString();
            applicationUrl = URLEncoder.encode(applicationUrl, "UTF-8");

            GetMethod getMethod = new GetMethod(properties.get(KOOKOO_OUTBOUND_URL).toString());
            getMethod.setQueryString(new NameValuePair[]{
                    new NameValuePair("api_key", properties.get(KOOKOO_API_KEY).toString()),
                    new NameValuePair("url", applicationUrl),
                    new NameValuePair("phone_no", callRequest.getPhone())
            });
            log.info("Dialing " + getMethod.getPath() + " " + getMethod.getQueryString());
            httpClient.executeMethod(getMethod);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
