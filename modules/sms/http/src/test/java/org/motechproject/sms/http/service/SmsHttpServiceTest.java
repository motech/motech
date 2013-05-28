package org.motechproject.sms.http.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.sms.api.SmsDeliveryFailureException;
import org.motechproject.sms.http.TemplateReader;
import org.motechproject.sms.http.template.Authentication;
import org.motechproject.sms.http.template.SmsHttpTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.api.constants.EventDataKeys.MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.RECIPIENT;
import static org.motechproject.sms.api.constants.EventSubjects.SMS_FAILURE_NOTIFICATION;

public class SmsHttpServiceTest {
    private static final String TEST_RECIPIENT = "0987654321";
    private static final String TEST_MESSAGE = "foo bar";

    @Mock
    private HttpClient httpClient;

    @Mock
    private TemplateReader templateReader;

    @Mock
    private MotechSchedulerService motechSchedulerService;

    @Mock
    private EventRelay eventRelay;

    private SmsHttpService smsHttpService;

    @Before
    public void setUp() {
        initMocks(this);
        smsHttpService = new SmsHttpService(httpClient, motechSchedulerService, templateReader, eventRelay);
    }

    @Test
    public void shouldMakeRequest() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(template.generateRequestFor(Arrays.asList(TEST_RECIPIENT), TEST_MESSAGE)).thenReturn(httpMethod);
        when(template.getSuccessfulResponsePattern()).thenReturn("sent");
        when(templateReader.getTemplate()).thenReturn(template);
        when(httpMethod.getResponseBodyAsString()).thenReturn("sent");

        smsHttpService.sendSms(asList(TEST_RECIPIENT), TEST_MESSAGE);

        verify(httpClient).executeMethod(httpMethod);
    }

    @Test
    public void shouldNotThrowExceptionIfResponseMessageWhenResponseHasExpectedSuccessMessage() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(template.getSuccessfulResponsePattern()).thenReturn("\\w+\\s+(?i)sent successfully");
        when(httpMethod.getResponseBodyAsString()).thenReturn("message senT successfully");
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(templateReader.getTemplate()).thenReturn(template);

        smsHttpService.sendSms(asList(TEST_RECIPIENT), TEST_MESSAGE);
    }

    @Test
    public void shouldRaiseFailureEventAndReleaseConnectionIfResponseIsNotASuccess() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(template.getSuccessfulResponsePattern()).thenReturn("\\w+\\s+(?i)sent successfully");
        when(httpMethod.getResponseBodyAsString()).thenReturn("boom");
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(templateReader.getTemplate()).thenReturn(template);

        smsHttpService.sendSms(asList(TEST_RECIPIENT), TEST_MESSAGE);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(RECIPIENT, TEST_RECIPIENT);
        parameters.put(MESSAGE, TEST_MESSAGE);
        MotechEvent event = new MotechEvent(SMS_FAILURE_NOTIFICATION, parameters);

        verify(httpMethod).releaseConnection();
        verify(eventRelay).sendEventMessage(event);
    }

    @Test
    public void shouldRaiseFailureEventWhenResponseIsNull() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);
        when(httpMethod.getResponseBodyAsString()).thenReturn(null);
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(templateReader.getTemplate()).thenReturn(template);

        smsHttpService.sendSms(asList("123", "456"), "foobar");

        HashMap<String, Object> parameters1 = new HashMap<>();
        parameters1.put(RECIPIENT, "123");
        parameters1.put(MESSAGE, "foobar");
        MotechEvent event1 = new MotechEvent(SMS_FAILURE_NOTIFICATION, parameters1);

        HashMap<String, Object> parameters2 = new HashMap<>();
        parameters2.put(RECIPIENT, "456");
        parameters2.put(MESSAGE, "foobar");
        MotechEvent event2 = new MotechEvent(SMS_FAILURE_NOTIFICATION, parameters2);

        ArgumentCaptor<HttpMethod> argumentCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        verify(httpClient).executeMethod(argumentCaptor.capture());
        assertEquals(httpMethod, argumentCaptor.getValue());

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);

        verify(eventRelay, times(2)).sendEventMessage(captor.capture());

        assertEquals(asList(event1, event2), captor.getAllValues());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfRecipientListIsNull() throws SmsDeliveryFailureException {
        smsHttpService.sendSms(null, "message");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfRecipientListIsEmpty() throws SmsDeliveryFailureException {
        smsHttpService.sendSms(new ArrayList<String>(), "message");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMessageIsNull() throws SmsDeliveryFailureException {
        smsHttpService.sendSms(Arrays.asList("123"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMessageIsEmpty() throws SmsDeliveryFailureException {
        smsHttpService.sendSms(Arrays.asList("123"), StringUtils.EMPTY);
    }

    @Test
    public void shouldAddAuthenticationInfoToHttpClient() throws SmsDeliveryFailureException, IOException {
        HttpMethod httpMethod = mock(HttpMethod.class);
        SmsHttpTemplate smsHttpTemplate = mock(SmsHttpTemplate.class);
        HttpClientParams httpClientParams = mock(HttpClientParams.class);
        HttpState httpClientState = mock(HttpState.class);

        when(httpMethod.getResponseBodyAsString()).thenReturn("success");
        when(smsHttpTemplate.getSuccessfulResponsePattern()).thenReturn("success");
        when(smsHttpTemplate.generateRequestFor(Arrays.asList("123"), "message")).thenReturn(httpMethod);
        when(smsHttpTemplate.getAuthentication()).thenReturn(new Authentication("username", "password"));
        when(templateReader.getTemplate()).thenReturn(smsHttpTemplate);
        when(httpClient.getParams()).thenReturn(httpClientParams);
        when(httpClient.getState()).thenReturn(httpClientState);

        smsHttpService.sendSms(Arrays.asList("123"), "message");

        verify(httpClientParams).setAuthenticationPreemptive(true);
        verify(httpClientState).setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("username", "password"));
    }



}
