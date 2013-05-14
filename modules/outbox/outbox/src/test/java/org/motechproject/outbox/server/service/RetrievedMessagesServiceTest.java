package org.motechproject.outbox.server.service;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.outbox.api.EventKeys;
import org.motechproject.outbox.api.domain.MessageRecord;
import org.motechproject.outbox.api.repository.AllMessageRecords;
import org.motechproject.outbox.server.service.RetrievedMessagesService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronJobId;
import org.motechproject.scheduler.domain.CronSchedulableJob;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RetrievedMessagesServiceTest {

    @InjectMocks
    private RetrievedMessagesService retrievedMessagesService = new RetrievedMessagesService();

    @Mock
    private MotechSchedulerService motechSchedulerService;

    @Mock
    private Properties outboxProperties;

    @Mock
    private AllMessageRecords allMessageRecords;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldScheduleRetrievedMessageJob() {
        String externalId = "1234";
        String language = "en";

        retrievedMessagesService.scheduleJob(externalId, language);
        ArgumentCaptor<CronSchedulableJob> captor =  ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).scheduleJob(captor.capture());

        CronSchedulableJob cronSchedulableJob = captor.getValue();
        assertEquals(EventKeys.getExternalID(cronSchedulableJob.getMotechEvent()), externalId);
        assertEquals(EventKeys.getScheduleJobIdKey(cronSchedulableJob.getMotechEvent()), String.format("outbox-%s-%s", externalId, language));
    }

    @Test
    public void shouldUnscheduleRetrievedMessageJob() {
        String externalId = "1234";
        String jobId = "4321";
        String cronValue = String.format("%s-%s%s", EventKeys.NOT_RETRIEVED_MESSAGE_SUBJECT, jobId, "");
        MessageRecord record = new MessageRecord(externalId, jobId);
        when(allMessageRecords.getMessageRecordByExternalId(externalId)).thenReturn(record);
        retrievedMessagesService.unscheduleJob(externalId);
        ArgumentCaptor<CronJobId> captor = ArgumentCaptor.forClass(CronJobId.class);
        verify(motechSchedulerService).unscheduleJob(captor.capture());
        Assert.assertEquals(captor.getValue().value(), cronValue);
    }
}
