package org.motechproject.ivr.kookoo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONObject;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.kookoo.service.KookooHttpRequestBuilder;
import org.motechproject.ivr.kookoo.service.OutboundResponseParser;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

@Service
public class KookooCallServiceImpl implements IVRService {
    public static final String OUTBOUND_URL = "kookoo.outbound.url";
    public static final String API_KEY = "kookoo.api.key";
    public static final String API_KEY_KEY = "api_key";
    public static final String URL_KEY = "url";
    public static final String CALLBACK_URL_KEY = "callback_url";
    public static final String PHONE_NUMBER_KEY = "phone_no";
    public static final String CUSTOM_DATA_KEY = "dataMap";
    public static final String IS_OUTBOUND_CALL = "is_outbound_call";

    private Properties properties;
    private HttpClient httpClient;
    private KookooCallDetailRecordsService kookooCallDetailRecordsService;
    private KookooHttpRequestBuilder kookooHttpRequestBuilder;
    private OutboundResponseParser responseParser;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    KookooCallServiceImpl(@Qualifier("ivrProperties") Properties properties, HttpClient httpClient, KookooHttpRequestBuilder kookooHttpRequestBuilder, OutboundResponseParser responseParser, KookooCallDetailRecordsService kookooCallDetailRecordsService) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.kookooHttpRequestBuilder = kookooHttpRequestBuilder;
        this.responseParser = responseParser;
        this.kookooCallDetailRecordsService = kookooCallDetailRecordsService;
    }

    @Override
    public void initiateCall(CallRequest callRequest) {
        if (callRequest == null) throw new IllegalArgumentException("Missing call request");

        GetMethod getMethod = null;
        String kooKooCallDetailRecordId = "";
        try {
            kooKooCallDetailRecordId = kookooCallDetailRecordsService.createOutgoing(callRequest.getPhone(), CallDetailRecord.Disposition.UNKNOWN);

            final String externalId = callRequest.getPayload().get(EXTERNAL_ID);
            callRequest.getPayload().put(IS_OUTBOUND_CALL, "true");
            callRequest.getPayload().put(KooKooIVRContext.CALL_DETAIL_RECORD_ID, kooKooCallDetailRecordId);
            JSONObject json = new JSONObject(callRequest.getPayload());


            String applicationCallbackUrl = String.format("%s/callback?%s=%s&%s=%s&%s=%s", callRequest.getCallBackUrl(), EXTERNAL_ID, externalId,
                    CALL_TYPE, callRequest.getPayload().get(CALL_TYPE), KooKooIVRContext.CALL_DETAIL_RECORD_ID, kooKooCallDetailRecordId);
            String applicationReplyUrl = String.format("%s?%s=%s", callRequest.getCallBackUrl(), CUSTOM_DATA_KEY, json.toString());

            getMethod = kookooHttpRequestBuilder.newGetMethod(properties.get(OUTBOUND_URL).toString(), new NameValuePair[]{
                    new NameValuePair(API_KEY_KEY, properties.get(API_KEY).toString()),
                    new NameValuePair(URL_KEY, applicationReplyUrl),
                    new NameValuePair(PHONE_NUMBER_KEY, callRequest.getPhone()),
                    new NameValuePair(CALLBACK_URL_KEY, applicationCallbackUrl)
            });

            log.info(String.format("Dialing %s", getMethod.getURI()));
            httpClient.executeMethod(getMethod);
            markAsFailedOnFailure(kooKooCallDetailRecordId, getMethod);
        } catch (IOException e) {
            log.error("Dialing Failed for phone number : " + callRequest.getPhone());
            throw new RuntimeException(e);
        } finally {
            if (getMethod != null) {
                getMethod.releaseConnection();
            }
        }

    }

    private void markAsFailedOnFailure(String kooKooCallDetailRecordId, GetMethod getMethod) {
        try {
            String response = getMethod.getResponseBodyAsString();
            if(responseParser.isError(response)) {
                kookooCallDetailRecordsService.setCallRecordAsFailed(kooKooCallDetailRecordId, responseParser.getMessage(response));
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
