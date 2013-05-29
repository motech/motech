package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.motechproject.testing.utils.BaseUnitTest;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.api.DeliveryStatus.PENDING;
import static org.motechproject.sms.api.SMSType.OUTBOUND;

public class SmsSendHandlerTest extends BaseUnitTest {
    private static final DateTime SEND_TIME = new DateTime(2013, 5, 29, 10, 25);

    private SmsSendHandler handler;

    @Mock
    private ManagedSmslibService managedSmslibService;

    @Mock
    private SmsAuditService smsAuditService;

    private ArgumentCaptor<SmsRecord> smsRecordCaptor = ArgumentCaptor.forClass(SmsRecord.class);

    @Before
    public void setup() {
        initMocks(this);
        mockCurrentDate(SEND_TIME);

        handler = new SmsSendHandler(managedSmslibService, smsAuditService);
    }

    @Test
    public void shouldListenToSmsSendEvent() throws NoSuchMethodException {
        Method handleMethod = SmsSendHandler.class.getDeclaredMethod("handle", new Class[]{MotechEvent.class});
        assertTrue("MotechListener annotation missing", handleMethod.isAnnotationPresent(MotechListener.class));
        MotechListener annotation = handleMethod.getAnnotation(MotechListener.class);
        assertArrayEquals(new String[]{EventSubjects.SEND_SMS}, annotation.subjects());
    }

    @Test
    public void shouldSendMessageUsingSmpp() throws Exception {
        handler.handle(new MotechEvent(EventSubjects.SEND_SMS, new HashMap<String, Object>() {{
            put(EventDataKeys.RECIPIENTS, Arrays.asList("0987654321"));
            put(EventDataKeys.MESSAGE, "foo bar");
        }}));
        verify(managedSmslibService).queueMessage(Arrays.asList("0987654321"), "foo bar");
        assertSmsRecord("foo bar", "0987654321");
    }

    @Test
    public void shouldScheduleMessageIfDeliveryTimeIsSpecified() throws Exception {
        handler.handle(new MotechEvent(EventSubjects.SEND_SMS, new HashMap<String, Object>() {{
            put(EventDataKeys.RECIPIENTS, Arrays.asList("0987654321"));
            put(EventDataKeys.MESSAGE, "foo bar");
            put(EventDataKeys.DELIVERY_TIME, new DateTime(2011, 12, 22, 4, 40, 0, 0));
        }}));
        verify(managedSmslibService).queueMessageAt(Arrays.asList("0987654321"), "foo bar", new DateTime(2011, 12, 22, 4, 40, 0, 0));
        assertSmsRecord("foo bar", "0987654321");
    }

    private void assertSmsRecord(String message, String recipient) {
        verify(smsAuditService).log(smsRecordCaptor.capture());

        SmsRecord record = smsRecordCaptor.getValue();

        assertEquals(OUTBOUND, record.getSmsType());
        assertEquals(message, record.getMessageContent());
        assertEquals(PENDING, record.getDeliveryStatus());
        assertEquals(SEND_TIME, record.getMessageTime());
        assertEquals(recipient, record.getPhoneNumber());
    }
}
