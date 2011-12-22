package org.motechproject.sms.kookoo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.SmsEventHandler;
import org.motechproject.sms.api.constants.EventKeys;
import org.motechproject.sms.api.constants.EventSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class SmsSendHandler implements SmsEventHandler {
    private static final Logger LOG = Logger.getLogger(SmsSendHandler.class);

    public static final String KOOKOO_OUTBOUND_SMS_URL = "kookoo.outbound.sms.url";
    public static final String KOOKOO_API_KEY = "kookoo.api.key";
    public static final String API_KEY_PARAM = "api_key";
    public static final String MESSAGE_PARAM = "message";
    public static final String PHONE_NO_PARAM = "phone_no";

    private HttpClient httpClient;
    private Properties properties;

    @Autowired
    public SmsSendHandler(@Qualifier("ivrProperties") Properties properties) {
        this(new HttpClient(), properties);
    }

    public SmsSendHandler(HttpClient httpClient, Properties properties) {
        this.httpClient = httpClient;
        this.properties = properties;
    }

    @Override
    @MotechListener(subjects = EventSubject.SEND_SMS)
    public void handle(MotechEvent motechEvent) throws Exception {
        final GetMethod request = new GetMethod(properties.getProperty(KOOKOO_OUTBOUND_SMS_URL));
        final String phoneNumber = ((List<String>) motechEvent.getParameters().get(EventKeys.RECIPIENTS)).get(0);
        final String message = (String) motechEvent.getParameters().get(EventKeys.MESSAGE);
        final String kooKooAPIKey = properties.getProperty(KOOKOO_API_KEY);

        request.setQueryString(new NameValuePair[]{
                new NameValuePair(API_KEY_PARAM, kooKooAPIKey),
                new NameValuePair(MESSAGE_PARAM, message),
                new NameValuePair(PHONE_NO_PARAM, phoneNumber),
        });
        try {
            int responseCode = httpClient.executeMethod(request);
            String response = request.getResponseBodyAsString();
            LOG.info(String.format("The message to:\n%s has been sent with\nresponsecode: %d\nresponse: %s", phoneNumber, responseCode, response));
        } catch (Exception e) {
            LOG.error(String.format("Message to:\n %s could not be sent.", phoneNumber));
            throw e;
        }
    }
}