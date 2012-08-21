package org.motechproject.sms.api.service;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.context.EventContext;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.event.EventRelay;
import org.motechproject.sms.api.MessageSplitter;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EventContext.class)
public class SmsServiceImplTest {
    @Mock
    private EventContext eventContext;
    @Mock
    private EventRelay eventRelay;
    @Mock
    private MotechSchedulerService motechSchedulerService;
    @Mock
    private Properties smsApiProperties;

    private SmsService smsService;
    private MessageSplitter messageSplitter;

    @Before
    public void setup() {
        initMocks(this);

        PowerMockito.mockStatic(EventContext.class);
        when(EventContext.getInstance()).thenReturn(eventContext);
        when(eventContext.getEventRelay()).thenReturn(eventRelay);
        when(smsApiProperties.getProperty("sms.schedule.future.sms")).thenReturn("true");
        when(smsApiProperties.getProperty("sms.multi.recipient.supported")).thenReturn("true");
        when(smsApiProperties.getProperty("sms.max.message.size")).thenReturn("160");

        messageSplitter = new MessageSplitter();
        smsService = new SmsServiceImpl(motechSchedulerService, messageSplitter, smsApiProperties);
    }

    @Test
    public void shouldRaiseSendSmsEventWithMessageAndRecipient() {
        smsService.sendSMS("9876543210", "This is a test message");

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());

        MotechEvent eventMessageSent = motechEventArgumentCaptor.getValue();
        assertEquals("This is a test message", (String) eventMessageSent.getParameters().get(EventDataKeys.MESSAGE));
        assertEquals(Arrays.asList("9876543210"), eventMessageSent.getParameters().get(EventDataKeys.RECIPIENTS));
    }

    @Test
    public void shouldRaiseSendSmsEventWithMessageMulitpleRecipients() {
        ArrayList<String> recipients = new ArrayList<String>() {{
            add("123");
            add("456");
            add("789");
        }};
        smsService.sendSMS(recipients, "This is a test message");

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());

        MotechEvent eventMessageSent = motechEventArgumentCaptor.getValue();
        assertEquals("This is a test message", (String) eventMessageSent.getParameters().get(EventDataKeys.MESSAGE));
        assertEquals(recipients, eventMessageSent.getParameters().get(EventDataKeys.RECIPIENTS));
    }

    @Test
    public void shouldRaiseSendSmsEvent_WhenDeliveryTimeIsNull() {
        smsService.sendSMS("123", "This is a test message", null);

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());

        MotechEvent eventMessageSent = motechEventArgumentCaptor.getValue();
        assertEquals("This is a test message", (String) eventMessageSent.getParameters().get(EventDataKeys.MESSAGE));
        assertEquals(Arrays.asList("123"), eventMessageSent.getParameters().get(EventDataKeys.RECIPIENTS));
    }

    @Test
    public void shouldRaiseTwoEventsIfMessageLengthIs170() {
        smsService.sendSMS("123", "This is a 160+ characters long message. All documentation is kept in javadocs because it guarantees consistency between what is on the web and what is in the source code.");

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(2)).sendEventMessage(motechEventArgumentCaptor.capture());

        List<MotechEvent> events = motechEventArgumentCaptor.getAllValues();
        assertEquals("Msg 1 of 2: This is a 160+ characters long message. All documentation is kept in javadocs because it guarantees consistency between what is on the web and wh...", events.get(0).getParameters().get(EventDataKeys.MESSAGE));
        assertEquals("Msg 2 of 2: at is in the source code.", events.get(1).getParameters().get(EventDataKeys.MESSAGE));
    }

    @Test
    public void shouldScheduleSendingOfSMSAtSpecificDeliveryTime() {
        DateTime deliveryTime = new DateTime(2011, 12, 23, 13, 50, 0, 0);
        smsService.sendSMS("123", "This is a test message", deliveryTime);

        ArgumentCaptor<RunOnceSchedulableJob> scheduledJobCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(motechSchedulerService).safeScheduleRunOnceJob(scheduledJobCaptor.capture());

        MotechEvent sendSmsEvent = scheduledJobCaptor.getValue().getMotechEvent();
        assertEquals("This is a test message", (String) sendSmsEvent.getParameters().get(EventDataKeys.MESSAGE));
        assertEquals(Arrays.asList("123"), sendSmsEvent.getParameters().get(EventDataKeys.RECIPIENTS));
        assertEquals(deliveryTime, sendSmsEvent.getParameters().get(EventDataKeys.DELIVERY_TIME));

        Date scheduledDeliveryDate = scheduledJobCaptor.getValue().getStartDate();
        assertEquals(deliveryTime.toDate(), scheduledDeliveryDate);
    }

    @Test
    public void shouldNotScheduleSendSmsEvent_WhenScheduleFutureSmsIsSetToBeFalse() {
        when(smsApiProperties.getProperty("sms.schedule.future.sms")).thenReturn("false");
        DateTime deliveryTime = new DateTime(2011, 12, 23, 13, 50, 0, 0);
        smsService.sendSMS("123", "This is a test message", deliveryTime);

        verify(motechSchedulerService, never()).safeScheduleRunOnceJob(Matchers.<RunOnceSchedulableJob>any());
        verify(eventRelay).sendEventMessage(Matchers.<MotechEvent>any());
    }

    @Test
    public void shouldScheduleTwoEventsIfMessageLengthIs170() {
        DateTime deliveryTime = new DateTime(2011, 12, 23, 13, 50, 0, 0);
        String part1 = "This is a 160+ characters long message. All documentation is kept in javadocs because it guarantees consistency between what is on the web and wh";
        String part2 = "at is in the source code.";
        smsService.sendSMS("123", part1 + part2, deliveryTime);

        ArgumentCaptor<RunOnceSchedulableJob> scheduledJobCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(motechSchedulerService, times(2)).safeScheduleRunOnceJob(scheduledJobCaptor.capture());

        List<RunOnceSchedulableJob> schedulableJobs = scheduledJobCaptor.getAllValues();
        assertEquals("Msg 1 of 2: " + part1 + "...", schedulableJobs.get(0).getMotechEvent().getParameters().get(EventDataKeys.MESSAGE));
        assertEquals("Msg 2 of 2: " + part2, schedulableJobs.get(1).getMotechEvent().getParameters().get(EventDataKeys.MESSAGE));
    }

    @Test
    public void shouldNotSplitMessagesIfMaxMessageSizePropertyIsNotDefined() {
        when(smsApiProperties.getProperty("sms.max.message.size")).thenReturn(null);

        DateTime deliveryTime = new DateTime(2011, 12, 23, 13, 50, 0, 0);
        String text = "This is a 160+ characters long message. All documentation is kept in javadocs because it guarantees consistency between what is on the web and what is in the source code.";
        smsService.sendSMS("123", text, deliveryTime);

        ArgumentCaptor<RunOnceSchedulableJob> scheduledJobCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(motechSchedulerService).safeScheduleRunOnceJob(scheduledJobCaptor.capture());

        RunOnceSchedulableJob schedulableJob = scheduledJobCaptor.getValue();
        assertEquals(text, schedulableJob.getMotechEvent().getParameters().get(EventDataKeys.MESSAGE));
    }

    @Test
    public void shouldRaiseMultipleSendSmsEvents_WhenMultiRecipientNotSupported() {
        when(smsApiProperties.getProperty("sms.multi.recipient.supported")).thenReturn("false");
        String message = "This is a test message";

        smsService.sendSMS(Arrays.asList("100", "200", "300"), message);

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(3)).sendEventMessage(motechEventArgumentCaptor.capture());

        List<MotechEvent> events = motechEventArgumentCaptor.getAllValues();
        assertEquals(Arrays.asList("100"), events.get(0).getParameters().get(EventDataKeys.RECIPIENTS));
        assertEquals(message, events.get(0).getParameters().get(EventDataKeys.MESSAGE));

        assertEquals(Arrays.asList("200"), events.get(1).getParameters().get(EventDataKeys.RECIPIENTS));
        assertEquals(message, events.get(1).getParameters().get(EventDataKeys.MESSAGE));

        assertEquals(Arrays.asList("300"), events.get(2).getParameters().get(EventDataKeys.RECIPIENTS));
        assertEquals(message, events.get(2).getParameters().get(EventDataKeys.MESSAGE));
    }
}
