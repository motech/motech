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
import org.motechproject.sms.http.SmsDeliveryFailureException;
import org.motechproject.sms.http.TemplateReader;
import org.motechproject.sms.http.template.Authentication;
import org.motechproject.sms.http.template.SmsHttpTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SmsHttpServiceTest {
    @Mock
    private HttpClient httpClient;
    @Mock
    private TemplateReader templateReader;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldMakeRequest() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(template.generateRequestFor(Arrays.asList("0987654321"), "foo bar")).thenReturn(httpMethod);
        when(template.getResponseSuccessCode()).thenReturn("sent");
        when(templateReader.getTemplate()).thenReturn(template);
        when(httpMethod.getResponseBodyAsString()).thenReturn("sent");

        SmsHttpService smsHttpService = new SmsHttpService(templateReader, httpClient);
        smsHttpService.sendSms(asList("0987654321"), "foo bar");

        verify(httpClient).executeMethod(httpMethod);
    }

    @Test
    public void shouldNotThrowExceptionIfResponseMessageWhenResponseHasExpectedSuccessMessage() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(httpMethod.getResponseBodyAsString()).thenReturn("message senT successfully");
        when(template.getResponseSuccessCode()).thenReturn("sent successfully");
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(templateReader.getTemplate()).thenReturn(template);

        SmsHttpService smsHttpService = new SmsHttpService(templateReader, httpClient);
        smsHttpService.sendSms(asList("0987654321"), "foo bar");
    }

    @Test(expected = SmsDeliveryFailureException.class)
    public void shouldThrowExceptionAndReleaseConnectionIfResponseIsNotASuccess() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(httpMethod.getResponseBodyAsString()).thenReturn("boom");
        when(template.getResponseSuccessCode()).thenReturn("sent");
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(templateReader.getTemplate()).thenReturn(template);

        SmsHttpService smsHttpService = new SmsHttpService(templateReader, httpClient);
        smsHttpService.sendSms(asList("0987654321"), "foo bar");
        verify(httpMethod).releaseConnection();
    }

    @Test(expected = SmsDeliveryFailureException.class)
    public void throwExceptionWhenResponseIsNull() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);
        when(httpMethod.getResponseBodyAsString()).thenReturn(null);
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(templateReader.getTemplate()).thenReturn(template);

        SmsHttpService smsHttpService = new SmsHttpService(templateReader, httpClient);
        smsHttpService.sendSms(asList("123", "456"), "foobar");

        ArgumentCaptor<HttpMethod> argumentCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        verify(httpClient).executeMethod(argumentCaptor.capture());
        assertEquals(httpMethod,argumentCaptor.getValue());
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfRecipientListIsNull() throws SmsDeliveryFailureException {
        SmsHttpService smsHttpService = new SmsHttpService(templateReader, httpClient);
        smsHttpService.sendSms(null, "message");
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfRecipientListIsEmpty() throws SmsDeliveryFailureException {
        SmsHttpService smsHttpService = new SmsHttpService(templateReader, httpClient);
        smsHttpService.sendSms(new ArrayList<String>(), "message");
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMessageIsNull() throws SmsDeliveryFailureException {
        SmsHttpService smsHttpService = new SmsHttpService(templateReader, httpClient);
        smsHttpService.sendSms(Arrays.asList("123"), null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMessageIsEmpty() throws SmsDeliveryFailureException {
        SmsHttpService smsHttpService = new SmsHttpService(templateReader, httpClient);
        smsHttpService.sendSms(Arrays.asList("123"), StringUtils.EMPTY);
    }

    @Test
    public void shouldAddAuthenticationInfoToHttpClient() throws SmsDeliveryFailureException, IOException {
        HttpMethod httpMethod = mock(HttpMethod.class);
        SmsHttpTemplate smsHttpTemplate = mock(SmsHttpTemplate.class);
        HttpClientParams httpClientParams = mock(HttpClientParams.class);
        HttpState httpClientState = mock(HttpState.class);

        when(httpMethod.getResponseBodyAsString()).thenReturn("success");
        when(smsHttpTemplate.getResponseSuccessCode()).thenReturn("success");
        when(smsHttpTemplate.generateRequestFor(Arrays.asList("123"), "message")).thenReturn(httpMethod);
        when(smsHttpTemplate.getAuthentication()).thenReturn(new Authentication("username", "password"));
        when(templateReader.getTemplate()).thenReturn(smsHttpTemplate);
        when(httpClient.getParams()).thenReturn(httpClientParams);
        when(httpClient.getState()).thenReturn(httpClientState);

        SmsHttpService smsHttpService = new SmsHttpService(templateReader, httpClient);
        smsHttpService.sendSms(Arrays.asList("123"), "message");

        verify(httpClientParams).setAuthenticationPreemptive(true);
        verify(httpClientState).setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("username", "password"));
    }
}
