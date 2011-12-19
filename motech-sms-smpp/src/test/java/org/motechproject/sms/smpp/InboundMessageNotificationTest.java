package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.smpp.constants.EventKeys;
import org.motechproject.sms.smpp.constants.EventSubject;
import org.smslib.InboundMessage;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class InboundMessageNotificationTest {

    int DONT_CARE = 0;

    InboundMessageNotification inboundMessageNotification;

    @Mock
    EventRelay eventRelay;

    @Before
    public void setup() {
        initMocks(this);
        inboundMessageNotification = new InboundMessageNotification(eventRelay);
    }

    @Test
    public void shouldRaiseEventWhenAnSmsIsReceived() {
        InboundMessage message = new InboundMessage(new DateTime(2011, 11, 23, 10, 20, 0, 0).toDate(), "sender", "yoohoo", DONT_CARE, null);
        inboundMessageNotification.process(null, null, message);

        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(eventCaptor.capture());

        MotechEvent event = eventCaptor.getValue();
        assertEquals(EventSubject.INBOUND_SMS, event.getSubject());
        assertEquals("sender", event.getParameters().get(EventKeys.SENDER));
        assertEquals("yoohoo", event.getParameters().get(EventKeys.MESSAGE));
        assertEquals(new DateTime(2011, 11, 23, 10, 20, 0, 0), event.getParameters().get(EventKeys.TIMESTAMP));
    }
}
