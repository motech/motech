package org.motechproject.sms.kookoo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.api.EventKeys;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSHandlerTest {
    private SMSHandler kookooSmsHandler;

    @Mock
    private HttpClient httpClient;
    @Mock
    private Properties properties;

    final String OUTBOUND_SMS_URL = "http://kookoo/url/for/sending/sms";

    final String API_KEY = "randomhashasKooKooKey";
    final String API_KEY_PARAM = "api_key";

    final String OUTBOUND_URL_PROPERTY_KEY = "kookoo.outbound.sms.url";
    final String API_KEY_PROPERTY_KEY = "kookoo.api.key";
    final String MESSAGE_PARAM = "message";
    final String PHONE_NO_PARAM = "phone_no";
    final String MESSAGE = "Test message for KooKoo";
    private MotechEvent motechEvent;

    @Before
    public void setup() {
        initMocks(this);
        when(properties.getProperty(OUTBOUND_URL_PROPERTY_KEY)).thenReturn(OUTBOUND_SMS_URL);
        when(properties.getProperty(API_KEY_PROPERTY_KEY)).thenReturn(API_KEY);
        kookooSmsHandler = new SMSHandler(httpClient, properties);

        final HashMap parameters = new HashMap(10);
        parameters.put(EventKeys.MESSAGE, MESSAGE);
        parameters.put(EventKeys.RECIPIENTS, Arrays.asList("987654321"));
        motechEvent = new MotechEvent(EventKeys.SEND_SMS, parameters);
    }

    @Test
    public void shouldSendSMSContactingKooKoo() throws Exception {
        kookooSmsHandler.sendSMS(motechEvent);

        final String expectedURL = String.format("%s?%s=%s&%s=%s&%s=%s", OUTBOUND_SMS_URL, API_KEY_PARAM, API_KEY, MESSAGE_PARAM, MESSAGE, PHONE_NO_PARAM, "987654321");
        System.out.println("ExpectedURL: " + expectedURL);
        verify(httpClient).executeMethod(argThat(new GetMethodMatcher(expectedURL)));
    }

    @Test
    public void shouldPropagateExceptionInCaseHttpCallThrowsException() throws  Exception {
        when(httpClient.executeMethod(Matchers.<HttpMethod>any())).thenThrow(new IOException("Some Unknown exception"));
        try {
            kookooSmsHandler.sendSMS(motechEvent);
            fail("Should have propagated exception, but has eaten it.");
        } catch(Exception e) {
            System.out.println("Exception was thrown as expected.");
        }
    }

    public class GetMethodMatcher extends ArgumentMatcher<GetMethod> {
        private String url;

        public GetMethodMatcher(String url) {
            this.url = url;
        }

        @Override
        public boolean matches(Object o) {
            GetMethod getMethod = (GetMethod) o;
            try {
                String actualURL = getMethod.getURI().getURI();
                System.out.println("Actual URL: " + actualURL);
                return actualURL.equals(url);
            } catch (URIException e) {
                return false;
            }
        }
    }

}