package org.motechproject.sms.api.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.api.EventKeys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSServiceTest {
    private SMSService smsService;

    @Mock
    private EventRelay eventRelay;

    @Before
    public void setup() {
        initMocks(this);
        smsService = new SMSService(eventRelay);
    }

    @Test
    public void shouldPutTheSMSOnEventRelay() {
        smsService.sendSMS("9876543210", "This is a test message");

        final ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());

        MotechEvent eventMessageSent = motechEventArgumentCaptor.getValue();
        assertEquals("This is a test message", (String) eventMessageSent.getParameters().get(EventKeys.MESSAGE));
        assertEquals(Arrays.asList("9876543210"), eventMessageSent.getParameters().get(EventKeys.RECIPIENTS));
    }

    @Test
    public void shouldSupportMulitpleRecipients() {
        ArrayList<String> recipients = new ArrayList<String>() {{
            add("123");
            add("456");
            add("789");
        }};
        smsService.sendSMS(recipients, "This is a test message");

        final ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());

        MotechEvent eventMessageSent = motechEventArgumentCaptor.getValue();
        assertEquals("This is a test message", (String) eventMessageSent.getParameters().get(EventKeys.MESSAGE));
        assertEquals(recipients, eventMessageSent.getParameters().get(EventKeys.RECIPIENTS));
    }
}