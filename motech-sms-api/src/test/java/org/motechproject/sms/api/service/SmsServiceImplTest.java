package org.motechproject.sms.api.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.api.service.SmsServiceImpl.MESSAGE;
import static org.motechproject.sms.api.service.SmsServiceImpl.RECIPIENTS;

public class SmsServiceImplTest {
    private SmsService smsService;

    @Mock
    private EventRelay eventRelay;

    @Before
    public void setup() {
        initMocks(this);
        smsService = new SmsServiceImpl(eventRelay);
    }

    @Test
    public void shouldPutTheSMSOnEventRelay() {
        smsService.sendSMS("9876543210", "This is a test message");

        final ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());

        MotechEvent eventMessageSent = motechEventArgumentCaptor.getValue();
        assertEquals("This is a test message", (String) eventMessageSent.getParameters().get(MESSAGE));
        assertEquals(Arrays.asList("9876543210"), eventMessageSent.getParameters().get(RECIPIENTS));
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
        assertEquals("This is a test message", (String) eventMessageSent.getParameters().get(MESSAGE));
        assertEquals(recipients, eventMessageSent.getParameters().get(RECIPIENTS));
    }
}