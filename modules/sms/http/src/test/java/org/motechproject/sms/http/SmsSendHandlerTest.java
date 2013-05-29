package org.motechproject.sms.http;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.sms.api.SmsDeliveryFailureException;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.motechproject.sms.http.service.SmsHttpService;
import org.motechproject.testing.utils.BaseUnitTest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.api.DeliveryStatus.PENDING;
import static org.motechproject.sms.api.SMSType.OUTBOUND;

public class SmsSendHandlerTest extends BaseUnitTest {
    private static final DateTime SEND_TIME = new DateTime(2013, 5, 29, 10, 25);

    private SmsSendHandler handler;

    @Mock
    private SmsHttpService smsHttpService;

    @Mock
    private SmsAuditService smsAuditService;

    private ArgumentCaptor<SmsRecord> smsRecordCaptor = ArgumentCaptor.forClass(SmsRecord.class);

    @Before
    public void setUp() {
        initMocks(this);

        mockCurrentDate(SEND_TIME);

        handler = new SmsSendHandler(smsHttpService, smsAuditService);
    }

    @Test
    public void shouldListenToSmsSendEvent() throws NoSuchMethodException {
        Method handleMethod = SmsSendHandler.class.getDeclaredMethod("handle", new Class[]{MotechEvent.class});
        assertTrue("MotechListener annotation missing", handleMethod.isAnnotationPresent(MotechListener.class));
        MotechListener annotation = handleMethod.getAnnotation(MotechListener.class);
        assertArrayEquals(new String[]{EventSubjects.SEND_SMS, EventSubjects.SEND_SMSDT}, annotation.subjects());
    }

    @Test
    public void shouldSendSmsBasedOnEventDetails() throws SmsDeliveryFailureException {
        HashMap<String, Object> eventParameters = new HashMap<String, Object>();
        List<String> recipients = asList("recipient_1", "recipient_2");
        String messageText = "message_text";

        eventParameters.put(EventDataKeys.RECIPIENTS, recipients);
        eventParameters.put(EventDataKeys.MESSAGE, messageText);

        MotechEvent motechEvent = new MotechEvent(EventSubjects.SEND_SMS, eventParameters);

        handler.handle(motechEvent);
        verify(smsHttpService).sendSms(recipients, messageText);
        assertSmsRecord("message_text", asList("recipient_1", "recipient_2"));
    }

    @Test
    public void shouldSendSmsIfDeliveryTimeIsSpecified() throws SmsDeliveryFailureException {
        HashMap<String, Object> eventParameters = new HashMap<String, Object>();
        List<String> recipients = asList("recipient_1", "recipient_2");
        String messageText = "message_text";

        eventParameters.put(EventDataKeys.RECIPIENTS, recipients);
        eventParameters.put(EventDataKeys.MESSAGE, messageText);
        eventParameters.put(EventDataKeys.DELIVERY_TIME, new DateTime(2013, 04, 05, 4, 40, 0, 0));

        MotechEvent motechEvent = new MotechEvent(EventSubjects.SEND_SMSDT, eventParameters);

        handler.handle(motechEvent);
        verify(smsHttpService).sendSms(recipients, messageText,new DateTime(2013, 04, 05, 4, 40, 0, 0));
        assertSmsRecord("message_text", asList("recipient_1", "recipient_2"));
    }

    private void assertSmsRecord(String message, List<String> recipients) {
        verify(smsAuditService, times(2)).log(smsRecordCaptor.capture());

        List<SmsRecord> records = smsRecordCaptor.getAllValues();

        for (SmsRecord record : records) {
            assertEquals(OUTBOUND, record.getSmsType());
            assertEquals(message, record.getMessageContent());
            assertEquals(PENDING, record.getDeliveryStatus());
            assertEquals(SEND_TIME, record.getMessageTime());
            assertThat(record.getPhoneNumber(), isIn(recipients));
        }
    }
}
