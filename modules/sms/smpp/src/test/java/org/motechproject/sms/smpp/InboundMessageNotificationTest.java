package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.sms.smpp.constants.EventSubjects;
import org.motechproject.sms.smpp.repository.AllInboundSMS;
import org.motechproject.sms.smpp.repository.AllOutboundSMS;
import org.motechproject.commons.date.util.DateUtil;
import org.smslib.AGateway;
import org.smslib.InboundMessage;
import org.smslib.StatusReportMessage;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.api.constants.EventDataKeys.INBOUND_MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.SENDER;
import static org.motechproject.sms.api.constants.EventDataKeys.TIMESTAMP;
import static org.smslib.Message.MessageTypes.INBOUND;
import static org.smslib.Message.MessageTypes.STATUSREPORT;
import static org.smslib.Message.MessageTypes.UNKNOWN;

public class InboundMessageNotificationTest {
    private InboundMessageNotification inboundMessageNotification;
    @Mock
    EventRelay eventRelay;
    @Mock
    AGateway gateway;
    @Mock
    AllInboundSMS allInboundSMS;
    @Mock
    AllOutboundSMS allOutboundSMS;

    @Before
    public void setup() {
        initMocks(this);
        inboundMessageNotification = new InboundMessageNotification(eventRelay, allInboundSMS, allOutboundSMS);
    }

    @Test
    public void shouldNotRespondToNonInboundMessages() {
        int dontCare = 0;
        InboundMessage message = new InboundMessage(new DateTime(2011, 11, 23, 10, 20, 0, 0).toDate(), "sender", "yoohoo", dontCare, null);
        inboundMessageNotification.process(gateway, UNKNOWN, message);

        verify(eventRelay, times(0)).sendEventMessage(Matchers.<MotechEvent>any());
    }

    @Test
    public void shouldRaiseEventWhenAnInboundSmsIsReceived() {
        int dontCare = 0;
        InboundMessage message = new InboundMessage(new DateTime(2011, 11, 23, 10, 20, 0, 0).toDate(), "sender", "yoohoo", dontCare, null);
        inboundMessageNotification.process(gateway, INBOUND, message);

        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(eventCaptor.capture());

        MotechEvent event = eventCaptor.getValue();
        assertEquals(EventSubjects.INBOUND_SMS, event.getSubject());
        assertEquals("sender", event.getParameters().get(SENDER));
        assertEquals("yoohoo", event.getParameters().get(INBOUND_MESSAGE));
        assertEquals(new DateTime(2011, 11, 23, 10, 20, 0, 0), event.getParameters().get(TIMESTAMP));
    }

    @Test
    public void shouldUpdateDeliveryStatusForInboundStatusMessage() {
        String refNo = "refNo";
        String srcAddress = "srcAddress";
        String destAddr = "destAddr";
        String text = "messageContent";
        StatusReportMessage reportMessage = new StatusReportMessage(refNo, srcAddress, destAddr, text, DateUtil.now().toDate(), DateUtil.now().toDate());
        StatusReportMessage.DeliveryStatuses deliveryStatus = StatusReportMessage.DeliveryStatuses.INPROGRESS;
        reportMessage.setStatus(deliveryStatus);

        inboundMessageNotification.process(gateway, STATUSREPORT, reportMessage);

        verify(allOutboundSMS).updateDeliveryStatus(destAddr, refNo, reportMessage.getStatus().name());
        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(eventCaptor.capture());
        MotechEvent event = eventCaptor.getValue();
        assertThat(event.getSubject(), is(EventSubjects.SMS_DELIVERY_REPORT));
    }

}
