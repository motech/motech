package org.motechproject.sms.api.service;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.api.MessageSplitter;
import org.motechproject.sms.api.event.SendSmsEvent;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class SmsServiceImplTest {
    @Mock
    private EventRelay eventRelay;
    @Mock
    private MotechSchedulerService schedulerService;
    @Mock
    private SettingsFacade settings;

    private SmsService smsService;
    private MessageSplitter messageSplitter;

    @Before
    public void setup() {
        initMocks(this);

        when(settings.getProperty("sms.schedule.future.sms")).thenReturn("true");
        when(settings.getProperty("sms.multi.recipient.supported")).thenReturn("true");
        when(settings.getProperty("sms.max.message.size")).thenReturn("160");

        messageSplitter = new MessageSplitter();
        smsService = new SmsServiceImpl(schedulerService, messageSplitter, settings, eventRelay);
    }

    @Test
    public void shouldRaiseSendSmsEventWithMessageAndRecipient() {
        smsService.sendSMS(new SendSmsRequest(asList("9876543210"), "This is a test message"));

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());

        SendSmsEvent sendSmsEvent = new SendSmsEvent(motechEventArgumentCaptor.getValue());
        assertEquals("This is a test message", sendSmsEvent.getMessage());
        assertEquals(asList("9876543210"), sendSmsEvent.getRecipients());
    }

    @Test
    public void shouldRaiseSendSmsEventWithMessageMultipleRecipients() {
        ArrayList<String> recipients = new ArrayList<String>() {{
            add("123");
            add("456");
            add("789");
        }};
        smsService.sendSMS(new SendSmsRequest(recipients, "This is a test message"));

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());

        SendSmsEvent sendSmsEvent = new SendSmsEvent(motechEventArgumentCaptor.getValue());
        assertEquals("This is a test message", sendSmsEvent.getMessage());
        assertEquals(recipients, sendSmsEvent.getRecipients());
    }

    @Test
    public void shouldRaiseSendSmsEvent_WhenDeliveryTimeIsNull() {
        smsService.sendSMS(new SendSmsRequest(asList("123"), "This is a test message", null));

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());

        SendSmsEvent sendSmsEvent = new SendSmsEvent(motechEventArgumentCaptor.getValue());
        assertEquals("This is a test message", sendSmsEvent.getMessage());
        assertEquals(asList("123"), sendSmsEvent.getRecipients());
    }

    @Test
    public void shouldRaiseTwoEventsIfMessageLengthIs170() {
        smsService.sendSMS(new SendSmsRequest(asList("123"), "This is a 160+ characters long message. All documentation is kept in javadocs because it guarantees consistency between what is on the web and what is in the source code."));

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(2)).sendEventMessage(motechEventArgumentCaptor.capture());

        List<MotechEvent> events = motechEventArgumentCaptor.getAllValues();
        SendSmsEvent sendSmsEvent1 = new SendSmsEvent(events.get(0));
        SendSmsEvent sendSmsEvent2 = new SendSmsEvent(events.get(1));
        assertEquals("Msg 1 of 2: This is a 160+ characters long message. All documentation is kept in javadocs because it guarantees consistency between what is on the web and wh...", sendSmsEvent1.getMessage());
        assertEquals("Msg 2 of 2: at is in the source code.", sendSmsEvent2.getMessage());
    }

    @Test
    public void shouldScheduleSendingOfSMSAtSpecificDeliveryTime() {
        DateTime deliveryTime = new DateTime(2011, 12, 23, 13, 50, 0, 0);
        smsService.sendSMS(new SendSmsRequest(asList("123"), "This is a test message", deliveryTime));

        ArgumentCaptor<RunOnceSchedulableJob> scheduledJobCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(schedulerService).safeScheduleRunOnceJob(scheduledJobCaptor.capture());

        SendSmsEvent sendSmsEvent = new SendSmsEvent(scheduledJobCaptor.getValue().getMotechEvent());
        assertEquals("This is a test message", sendSmsEvent.getMessage());
        assertEquals(asList("123"), sendSmsEvent.getRecipients());
        assertEquals(deliveryTime, sendSmsEvent.getDeliveryTime());

        Date scheduledDeliveryDate = scheduledJobCaptor.getValue().getStartDate();
        assertEquals(deliveryTime.toDate(), scheduledDeliveryDate);
    }

    @Test
    public void shouldNotScheduleSendSmsEvent_WhenScheduleFutureSmsIsSetToBeFalse() {
        when(settings.getProperty("sms.schedule.future.sms")).thenReturn("false");
        DateTime deliveryTime = new DateTime(2011, 12, 23, 13, 50, 0, 0);
        smsService.sendSMS(new SendSmsRequest(asList("123"), "This is a test message", deliveryTime));

        verify(schedulerService, never()).safeScheduleRunOnceJob(Matchers.<RunOnceSchedulableJob>any());
        verify(eventRelay).sendEventMessage(Matchers.<MotechEvent>any());
    }

    @Test
    public void shouldScheduleTwoEventsIfMessageLengthIs170() {
        DateTime deliveryTime = new DateTime(2011, 12, 23, 13, 50, 0, 0);
        String part1 = "This is a 160+ characters long message. All documentation is kept in javadocs because it guarantees consistency between what is on the web and wh";
        String part2 = "at is in the source code.";
        smsService.sendSMS(new SendSmsRequest(Arrays.asList("123"), part1 + part2, deliveryTime));

        ArgumentCaptor<RunOnceSchedulableJob> scheduledJobCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(schedulerService, times(2)).safeScheduleRunOnceJob(scheduledJobCaptor.capture());

        List<RunOnceSchedulableJob> schedulableJobs = scheduledJobCaptor.getAllValues();
        SendSmsEvent sendSmsEvent1 = new SendSmsEvent(schedulableJobs.get(0).getMotechEvent());
        SendSmsEvent sendSmsEvent2 = new SendSmsEvent(schedulableJobs.get(1).getMotechEvent());
        assertEquals("Msg 1 of 2: " + part1 + "...", sendSmsEvent1.getMessage());
        assertEquals("Msg 2 of 2: " + part2, sendSmsEvent2.getMessage());
    }

    @Test
    public void shouldNotSplitMessagesIfMaxMessageSizePropertyIsNotDefined() {
        when(settings.getProperty("sms.max.message.size")).thenReturn(null);

        DateTime deliveryTime = new DateTime(2011, 12, 23, 13, 50, 0, 0);
        String text = "This is a 160+ characters long message. All documentation is kept in javadocs because it guarantees consistency between what is on the web and what is in the source code.";
        smsService.sendSMS(new SendSmsRequest(asList("123"), text, deliveryTime));

        ArgumentCaptor<RunOnceSchedulableJob> scheduledJobCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(schedulerService).safeScheduleRunOnceJob(scheduledJobCaptor.capture());

        SendSmsEvent sendSmsEvent = new SendSmsEvent(scheduledJobCaptor.getValue().getMotechEvent());
        assertEquals(text, sendSmsEvent.getMessage());
    }

    @Test
    public void shouldRaiseMultipleSendSmsEvents_WhenMultiRecipientNotSupported() {
        when(settings.getProperty("sms.multi.recipient.supported")).thenReturn("false");
        String message = "This is a test message";

        smsService.sendSMS(new SendSmsRequest(asList("100", "200", "300"), message));

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(3)).sendEventMessage(motechEventArgumentCaptor.capture());

        List<MotechEvent> events = motechEventArgumentCaptor.getAllValues();
        SendSmsEvent sendSmsEvent1 = new SendSmsEvent(events.get(0));
        assertEquals(asList("100"), sendSmsEvent1.getRecipients());
        assertEquals(message, sendSmsEvent1.getMessage());

        SendSmsEvent sendSmsEvent2 = new SendSmsEvent(events.get(1));
        assertEquals(asList("200"), sendSmsEvent2.getRecipients());
        assertEquals(message, sendSmsEvent2.getMessage());

        SendSmsEvent sendSmsEvent3 = new SendSmsEvent(events.get(2));
        assertEquals(asList("300"), sendSmsEvent3.getRecipients());
        assertEquals(message, sendSmsEvent3.getMessage());
    }
}
