package org.motechproject.outbox.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.event.MotechEvent;
import org.motechproject.ivr.service.contract.CallRequest;
import org.motechproject.ivr.service.contract.IVRService;
import org.motechproject.outbox.api.EventKeys;
import org.motechproject.outbox.server.service.RetrievedMessagesService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.JobId;

import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OutboxExecutionHandlerTest {
    @InjectMocks
    private OutboxExecutionHandler outboxExecutionHandler = new OutboxExecutionHandler();

    @Mock
    IVRService ivrServiceMock;

    @Mock
    Properties outboxProperties;

    @Mock
    private MotechSchedulerService motechSchedulerService;

    @Mock
    private RetrievedMessagesService retrievedMessagesService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecute() {
        MotechEvent event = new MotechEvent("", null);
        event.getParameters().put(EventKeys.PHONE_NUMBER_KEY, "SIP/1000");
        event.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "pID");
        event.getParameters().put(EventKeys.LANGUAGE_KEY, "en");

        outboxExecutionHandler.execute(event);

        verify(retrievedMessagesService).scheduleJob(EventKeys.getExternalID(event), EventKeys.getLanguageKey(event));
        verify(ivrServiceMock).initiateCall(any(CallRequest.class));
    }

    @Test
    public void testExecute_NoPhone() {
        MotechEvent event = new MotechEvent("", null);
        event.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "pID");

        outboxExecutionHandler.execute(event);

        verify(ivrServiceMock, times(0)).initiateCall(any(CallRequest.class));
    }

    @Test
    public void testExecute_NoPartyID() {
        MotechEvent event = new MotechEvent("", null);
        event.getParameters().put(EventKeys.PHONE_NUMBER_KEY, "SIP/1000");

        outboxExecutionHandler.execute(event);

        verify(ivrServiceMock, times(0)).initiateCall(any(CallRequest.class));
    }

    @Test
    public void testSchedule() {
        MotechEvent event = new MotechEvent("", null);
        event.getParameters().put(EventKeys.CALL_HOUR_KEY, 12);
        event.getParameters().put(EventKeys.CALL_MINUTE_KEY, 0);
        event.getParameters().put(EventKeys.PHONE_NUMBER_KEY, "SIP/1000");
        event.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "pID");

        outboxExecutionHandler.schedule(event);

        verify(motechSchedulerService).scheduleJob(any(CronSchedulableJob.class));
    }

    @Test
    public void testSchedule_NoPhone() {
        MotechEvent event = new MotechEvent("", null);
        event.getParameters().put(EventKeys.CALL_HOUR_KEY, 12);
        event.getParameters().put(EventKeys.CALL_MINUTE_KEY, 0);
        event.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "pID");

        outboxExecutionHandler.schedule(event);

        verify(motechSchedulerService, times(0)).scheduleJob(any(CronSchedulableJob.class));
    }

    @Test
    public void testSchedule_NoParty() {
        MotechEvent event = new MotechEvent("", null);
        event.getParameters().put(EventKeys.CALL_HOUR_KEY, 12);
        event.getParameters().put(EventKeys.CALL_MINUTE_KEY, 0);
        event.getParameters().put(EventKeys.PHONE_NUMBER_KEY, "SIP/1000");

        outboxExecutionHandler.schedule(event);

        verify(motechSchedulerService, times(0)).scheduleJob(any(CronSchedulableJob.class));
    }

    @Test
    public void testSchedule_NoHour() {
        MotechEvent event = new MotechEvent("", null);
        event.getParameters().put(EventKeys.CALL_MINUTE_KEY, 0);
        event.getParameters().put(EventKeys.PHONE_NUMBER_KEY, "SIP/1000");
        event.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "pID");

        outboxExecutionHandler.schedule(event);

        verify(motechSchedulerService, times(0)).scheduleJob(any(CronSchedulableJob.class));
    }

    @Test
    public void testSchedule_InvalidHour() {
        MotechEvent event = new MotechEvent("", null);
        event.getParameters().put(EventKeys.CALL_HOUR_KEY, "foo");
        event.getParameters().put(EventKeys.CALL_MINUTE_KEY, 0);
        event.getParameters().put(EventKeys.PHONE_NUMBER_KEY, "SIP/1000");
        event.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "pID");

        outboxExecutionHandler.schedule(event);

        verify(motechSchedulerService, times(0)).scheduleJob(any(CronSchedulableJob.class));
    }

    @Test
    public void testSchedule_NoMinute() {
        MotechEvent event = new MotechEvent("", null);
        event.getParameters().put(EventKeys.CALL_HOUR_KEY, 12);
        event.getParameters().put(EventKeys.PHONE_NUMBER_KEY, "SIP/1000");
        event.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "pID");

        outboxExecutionHandler.schedule(event);

        verify(motechSchedulerService, times(0)).scheduleJob(any(CronSchedulableJob.class));
    }

    @Test
    public void testSchedule_InvalideMinute() {
        MotechEvent event = new MotechEvent("", null);
        event.getParameters().put(EventKeys.CALL_HOUR_KEY, 12);
        event.getParameters().put(EventKeys.CALL_MINUTE_KEY, "foo");
        event.getParameters().put(EventKeys.PHONE_NUMBER_KEY, "SIP/1000");
        event.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "pID");

        outboxExecutionHandler.schedule(event);

        verify(motechSchedulerService, times(0)).scheduleJob(any(CronSchedulableJob.class));
    }

    @Test
    public void testUnschedule() {
        MotechEvent event = new MotechEvent("", null);
        event.getParameters().put(EventKeys.SCHEDULE_JOB_ID_KEY, "JobId");

        outboxExecutionHandler.unschedule(event);

        ArgumentCaptor<JobId> captor = ArgumentCaptor.forClass(JobId.class);

        verify(motechSchedulerService).unscheduleJob(captor.capture());
    }

    @Test
    public void testUnschedule_NoPhone() {
        MotechEvent event = new MotechEvent("", null);

        outboxExecutionHandler.unschedule(event);

        verify(motechSchedulerService, times(0)).unscheduleJob(any(JobId.class));
    }
}
