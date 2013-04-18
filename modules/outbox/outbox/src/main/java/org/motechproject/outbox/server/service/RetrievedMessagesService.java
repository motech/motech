package org.motechproject.outbox.server.service;

import org.motechproject.event.MotechEvent;
import org.motechproject.outbox.api.EventKeys;
import org.motechproject.outbox.api.domain.MessageRecord;
import org.motechproject.outbox.api.repository.AllMessageRecords;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronJobId;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;

@Service
public class RetrievedMessagesService {

    @Autowired
    private MotechSchedulerService motechSchedulerService;

    @Autowired
    private Properties outboxProperties;

    @Autowired
    private AllMessageRecords allMessageRecords;

    public static final String RETRIEVED_TIMEOUT = "retrival.timeout";

    public void scheduleJob(String externalId, String language) {
        Map<String, Object> param = new HashMap<>();
        String generatedJobId = "outbox-" + externalId + "-" + language;

        param.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        param.put(EventKeys.SCHEDULE_JOB_ID_KEY, generatedJobId);

        MotechEvent reminderEvent = new MotechEvent(EventKeys.NOT_RETRIEVED_MESSAGE_SUBJECT, param);
        allMessageRecords.addOrUpdateMessageRecord(new MessageRecord(externalId, generatedJobId));

        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(reminderEvent, format("%s 0 0 0 0 0", outboxProperties.getProperty(RETRIEVED_TIMEOUT)));

        motechSchedulerService.scheduleJob(cronSchedulableJob);
    }

    public void unscheduleJob(String externalId) {
        MessageRecord record = allMessageRecords.getMessageRecordByExternalId(externalId);
        if (record != null) {
            motechSchedulerService.unscheduleJob(new CronJobId(EventKeys.NOT_RETRIEVED_MESSAGE_SUBJECT, record.getJobId()));
            allMessageRecords.remove(record);
        }
    }
}
