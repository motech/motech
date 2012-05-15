package org.motechproject.sms.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.sms.http.service.SmsHttpService;

import java.io.IOException;
import java.util.Arrays;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.http.SmsHttpTemplate.Response;

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
        SmsHttpTemplate.Outgoing outgoing = new SmsHttpTemplate.Outgoing();
        Response response = new Response();
        response.setSuccess("sent");
        outgoing.setResponse(response);

        when(template.generateRequestFor(Arrays.asList("0987654321"), "foo bar")).thenReturn(httpMethod);
        when(template.getOutgoing()).thenReturn(outgoing);
        when(templateReader.getTemplate(anyString())).thenReturn(template);
        when(httpMethod.getResponseBodyAsString()).thenReturn("sent");

        SmsHttpService smsHttpService = new SmsHttpService(templateReader, httpClient);
        smsHttpService.sendSms(asList("0987654321"), "foo bar");

        verify(httpClient).executeMethod(httpMethod);
    }

    @Test
    public void shouldNotThrowExceptionIfResponseMessageWhenResponseHasExpectedSuccessMessage() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate.Outgoing outgoing = new SmsHttpTemplate.Outgoing();
        Response response = new Response();
        response.setSuccess("Sent");
        outgoing.setResponse(response);

        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(httpMethod.getResponseBodyAsString()).thenReturn("message senT successfully");
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(template.getOutgoing()).thenReturn(outgoing);
        when(templateReader.getTemplate(Matchers.<String>any())).thenReturn(template);

        SmsHttpService smsHttpService = new SmsHttpService(templateReader, httpClient);
        smsHttpService.sendSms(asList("0987654321"), "foo bar");
    }

    @Test
    public void shouldNotThrowExceptionIfResponseMessageContainsTheExpectedSuccessMessage() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate.Outgoing outgoing = new SmsHttpTemplate.Outgoing();
        Response response = new Response();
        response.setSuccess("part of success");
        outgoing.setResponse(response);

        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(httpMethod.getResponseBodyAsString()).thenReturn("real response containing the phrase part of success and more stuff");
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(template.getOutgoing()).thenReturn(outgoing);
        when(templateReader.getTemplate(Matchers.<String>any())).thenReturn(template);

        SmsHttpService smsHttpService = new SmsHttpService(templateReader, httpClient);
        smsHttpService.sendSms(asList("0987654321"), "foo bar");
    }

    @Test(expected = SmsDeliveryFailureException.class)
    public void shouldThrowExceptionAndReleaseConnectionIfResponseIsNotASuccess() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate.Outgoing outgoing = new SmsHttpTemplate.Outgoing();
        Response response = new Response();
        response.setSuccess("sent");
        outgoing.setResponse(response);

        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(httpMethod.getResponseBodyAsString()).thenReturn("boom");
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(template.getOutgoing()).thenReturn(outgoing);
        when(templateReader.getTemplate(Matchers.<String>any())).thenReturn(template);

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
        when(templateReader.getTemplate(Matchers.<String>any())).thenReturn(template);

        SmsHttpService smsHttpService = new SmsHttpService(templateReader, httpClient);
        smsHttpService.sendSms(asList("123", "456"), "foobar");

        ArgumentCaptor<HttpMethod> argumentCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        verify(httpClient).executeMethod(argumentCaptor.capture());
        assertEquals(httpMethod,argumentCaptor.getValue());
    }
}
