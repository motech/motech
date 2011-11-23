package org.motechproject.sms.api.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.api.EventKeys;

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
        final String NUMBER = "9876543210";
        final String MESSAGE = "This is a test message";

        smsService.sendSMS(NUMBER, MESSAGE);

        final ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());

        MotechEvent eventMessageSent = motechEventArgumentCaptor.getValue();
        final String messageSent = (String) eventMessageSent.getParameters().get(EventKeys.MESSAGE);
        final String numberToWhichMessageWasSent = (String) eventMessageSent.getParameters().get(EventKeys.NUMBER);

        assertThat(messageSent, is(MESSAGE));
        assertThat(numberToWhichMessageWasSent, is(NUMBER));
    }
}