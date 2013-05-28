package org.motechproject.sms.http.web;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.motechproject.sms.http.TemplateReader;
import org.motechproject.sms.http.template.Incoming;
import org.motechproject.sms.http.template.SmsHttpTemplate;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.api.DeliveryStatus.RECEIVED;
import static org.motechproject.sms.api.SMSType.INBOUND;
import static org.motechproject.sms.api.constants.EventDataKeys.INBOUND_MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.TIMESTAMP;

public class InboundSmsControllerTest {
    @Mock
    private EventRelay eventRelay;

    @Mock
    private TemplateReader templateReader;

    @Mock
    private SmsAuditService smsAuditService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void handleIncomingSms() {
        SmsHttpTemplate template = new SmsHttpTemplate();
        Incoming incoming = new Incoming();
        incoming.setMessageKey("message");
        incoming.setSenderKey("sender");
        incoming.setTimestampKey("timestamp");
        template.setIncoming(incoming);
        when(templateReader.getTemplate()).thenReturn(template);

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getParameter(template.getIncoming().getMessageKey())).thenReturn("some text message");
        when(httpServletRequest.getParameter(template.getIncoming().getSenderKey())).thenReturn("1234567890");
        when(httpServletRequest.getParameter(template.getIncoming().getTimestampKey())).thenReturn("2013-05-28T14:48");

        new InboundSmsController(templateReader, eventRelay, smsAuditService).handle(httpServletRequest);

        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(eventCaptor.capture());

        MotechEvent event = eventCaptor.getValue();
        assertEquals(EventSubjects.INBOUND_SMS, event.getSubject());
        assertEquals("some text message", event.getParameters().get(INBOUND_MESSAGE));
        assertEquals("1234567890", event.getParameters().get(EventDataKeys.SENDER));
        assertEquals("2013-05-28T14:48", event.getParameters().get(TIMESTAMP));

        assertSmsRecord("some text message", "1234567890", new DateTime(2013, 5, 28, 14, 48));
    }

    @Test
    public void timestampIsOptionalForIncomingMessage() {
        SmsHttpTemplate template = new SmsHttpTemplate();
        Incoming incoming = new Incoming();
        incoming.setMessageKey("message");
        incoming.setSenderKey("sender");
        template.setIncoming(incoming);
        when(templateReader.getTemplate()).thenReturn(template);

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getParameter(template.getIncoming().getMessageKey())).thenReturn("some text message");
        when(httpServletRequest.getParameter(template.getIncoming().getSenderKey())).thenReturn("1234567890");

        new InboundSmsController(templateReader, eventRelay, smsAuditService).handle(httpServletRequest);

        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(eventCaptor.capture());

        MotechEvent event = eventCaptor.getValue();
        assertEquals(null, event.getParameters().get(TIMESTAMP));

        assertSmsRecord("some text message", "1234567890", null);
    }

    private void assertSmsRecord(String message, String recipient, DateTime sendTime) {
        ArgumentCaptor<SmsRecord> smsRecordCaptor = ArgumentCaptor.forClass(SmsRecord.class);
        verify(smsAuditService).log(smsRecordCaptor.capture());

        SmsRecord actual = smsRecordCaptor.getValue();

        assertEquals(message, actual.getMessageContent());
        assertEquals(recipient, actual.getPhoneNumber());
        assertEquals(RECEIVED, actual.getDeliveryStatus());
        assertEquals(sendTime, actual.getMessageTime());
        assertEquals(INBOUND, actual.getSmsType());
    }
}
