package org.motechproject.sms.http.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.SmsDeliveryFailureException;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.motechproject.sms.http.TemplateReader;
import org.motechproject.sms.http.template.Authentication;
import org.motechproject.sms.http.template.SmsHttpTemplate;
import org.motechproject.testing.utils.BaseUnitTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.api.DeliveryStatus.DELIVERED;
import static org.motechproject.sms.api.DeliveryStatus.KEEPTRYING;
import static org.motechproject.sms.api.SMSType.OUTBOUND;
import static org.motechproject.sms.api.constants.EventDataKeys.FAILURE_COUNT;
import static org.motechproject.sms.api.constants.EventDataKeys.MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.RECIPIENT;
import static org.motechproject.sms.api.constants.EventSubjects.SMS_FAILURE_NOTIFICATION;

public class SmsHttpServiceTest extends BaseUnitTest {
    private static final String TEST_RECIPIENT = "0987654321";
    private static final String TEST_MESSAGE = "foo bar";
    private static final Integer TEST_FAILURE_COUNT = 2;
    private static final DateTime SEND_TIME = new DateTime(2013, 5, 28, 15, 12);

    @Mock
    private HttpClient httpClient;

    @Mock
    private TemplateReader templateReader;

    @Mock
    private MotechSchedulerService motechSchedulerService;

    @Mock
    private EventRelay eventRelay;

    @Mock
    private SmsAuditService smsAuditService;

    @Mock
    private SettingsFacade settingsFacade;

    private SmsHttpService smsHttpService;
    private ArgumentCaptor<SmsRecord> smsRecordCaptor = ArgumentCaptor.forClass(SmsRecord.class);

    @Before
    public void setUp() {
        initMocks(this);
        mockCurrentDate(SEND_TIME);

        when(settingsFacade.getProperty("max_retries")).thenReturn("5");

        smsHttpService = new SmsHttpService(eventRelay, httpClient, motechSchedulerService, settingsFacade, smsAuditService, templateReader);
    }

    @Test
    public void shouldMakeRequest() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(template.generateRequestFor(Arrays.asList(TEST_RECIPIENT), TEST_MESSAGE)).thenReturn(httpMethod);
        when(template.getSuccessfulResponsePattern()).thenReturn("sent");
        when(templateReader.getTemplate()).thenReturn(template);
        when(httpMethod.getResponseBodyAsString()).thenReturn("sent");

        smsHttpService.sendSms(asList(TEST_RECIPIENT), TEST_MESSAGE, TEST_FAILURE_COUNT);

        verify(httpClient).executeMethod(httpMethod);

        assertSmsRecord(times(1), TEST_MESSAGE, asList(TEST_RECIPIENT), DELIVERED);
    }

    @Test
    public void shouldNotThrowExceptionIfResponseMessageWhenResponseHasExpectedSuccessMessage() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(template.getSuccessfulResponsePattern()).thenReturn("\\w+\\s+(?i)sent successfully");
        when(httpMethod.getResponseBodyAsString()).thenReturn("message senT successfully");
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(templateReader.getTemplate()).thenReturn(template);

        smsHttpService.sendSms(asList(TEST_RECIPIENT), TEST_MESSAGE, TEST_FAILURE_COUNT);

        assertSmsRecord(times(1), TEST_MESSAGE, asList(TEST_RECIPIENT), DELIVERED);
    }

    @Test
    public void shouldRaiseFailureEventAndReleaseConnectionIfResponseIsNotASuccess() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);

        when(template.getSuccessfulResponsePattern()).thenReturn("\\w+\\s+(?i)sent successfully");
        when(httpMethod.getResponseBodyAsString()).thenReturn("boom");
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(templateReader.getTemplate()).thenReturn(template);

        smsHttpService.sendSms(asList(TEST_RECIPIENT), TEST_MESSAGE, TEST_FAILURE_COUNT);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(RECIPIENT, TEST_RECIPIENT);
        parameters.put(MESSAGE, TEST_MESSAGE);
        parameters.put(FAILURE_COUNT, TEST_FAILURE_COUNT + 1);
        MotechEvent event = new MotechEvent(SMS_FAILURE_NOTIFICATION, parameters);

        verify(httpMethod).releaseConnection();
        verify(eventRelay).sendEventMessage(event);

        assertSmsRecord(times(1), TEST_MESSAGE, asList(TEST_RECIPIENT), KEEPTRYING);
    }

    @Test
    public void shouldRaiseFailureEventWhenResponseIsNull() throws IOException, SmsDeliveryFailureException {
        SmsHttpTemplate template = mock(SmsHttpTemplate.class);
        GetMethod httpMethod = mock(GetMethod.class);
        when(httpMethod.getResponseBodyAsString()).thenReturn(null);
        when(template.generateRequestFor(anyList(), anyString())).thenReturn(httpMethod);
        when(templateReader.getTemplate()).thenReturn(template);

        smsHttpService.sendSms(asList("123", "456"), "foobar", TEST_FAILURE_COUNT);

        HashMap<String, Object> parameters1 = new HashMap<>();
        parameters1.put(RECIPIENT, "123");
        parameters1.put(MESSAGE, "foobar");
        parameters1.put(FAILURE_COUNT, TEST_FAILURE_COUNT + 1);
        MotechEvent event1 = new MotechEvent(SMS_FAILURE_NOTIFICATION, parameters1);

        HashMap<String, Object> parameters2 = new HashMap<>();
        parameters2.put(RECIPIENT, "456");
        parameters2.put(MESSAGE, "foobar");
        parameters2.put(FAILURE_COUNT, TEST_FAILURE_COUNT + 1);
        MotechEvent event2 = new MotechEvent(SMS_FAILURE_NOTIFICATION, parameters2);

        ArgumentCaptor<HttpMethod> argumentCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        verify(httpClient).executeMethod(argumentCaptor.capture());
        assertEquals(httpMethod, argumentCaptor.getValue());

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);

        verify(eventRelay, times(2)).sendEventMessage(captor.capture());

        assertEquals(asList(event1, event2), captor.getAllValues());

        assertSmsRecord(times(2), "foobar", asList("123", "456"), KEEPTRYING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfRecipientListIsNull() throws SmsDeliveryFailureException {
        smsHttpService.sendSms(null, "message", TEST_FAILURE_COUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfRecipientListIsEmpty() throws SmsDeliveryFailureException {
        smsHttpService.sendSms(new ArrayList<String>(), "message", TEST_FAILURE_COUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMessageIsNull() throws SmsDeliveryFailureException {
        smsHttpService.sendSms(Arrays.asList("123"), null, TEST_FAILURE_COUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMessageIsEmpty() throws SmsDeliveryFailureException {
        smsHttpService.sendSms(Arrays.asList("123"), StringUtils.EMPTY, TEST_FAILURE_COUNT);
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

        smsHttpService.sendSms(Arrays.asList("123"), "message", TEST_FAILURE_COUNT);

        verify(httpClientParams).setAuthenticationPreemptive(true);
        verify(httpClientState).setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("username", "password"));

        assertSmsRecord(times(1), "message", Arrays.asList("123"), DELIVERED);
    }

    private void assertSmsRecord(VerificationMode times, String message, List<String> recipients, DeliveryStatus deliveryStatus) {
        verify(smsAuditService, times).log(smsRecordCaptor.capture());

        List<SmsRecord> records = smsRecordCaptor.getAllValues();

        for (SmsRecord record : records) {
            assertEquals(OUTBOUND, record.getSmsType());
            assertEquals(message, record.getMessageContent());
            assertEquals(deliveryStatus, record.getDeliveryStatus());
            assertEquals(SEND_TIME, record.getMessageTime());
            assertThat(record.getPhoneNumber(), isIn(recipients));
        }
    }
}
