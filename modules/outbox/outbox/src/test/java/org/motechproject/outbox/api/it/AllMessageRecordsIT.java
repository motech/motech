package org.motechproject.outbox.api.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.outbox.api.domain.MessageRecord;
import org.motechproject.outbox.api.repository.AllMessageRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class AllMessageRecordsIT {

    @Autowired
    private AllMessageRecords allMessageRecords;

    @Before
    public void setUp() throws Exception {
        allMessageRecords.removeAll();
    }

    @After
    public void tearDown() throws Exception {
        allMessageRecords.removeAll();
    }

    @Test
    public void shouldAddMessageRecord() {
        String externalId = "1234";
        String jobId = "4321";
        MessageRecord record = new MessageRecord(externalId, jobId);
        allMessageRecords.addOrUpdateMessageRecord(record);
        MessageRecord recordFromDataBase = allMessageRecords.getMessageRecordByExternalId(externalId);
        assertEquals(recordFromDataBase.getExternalId(), record.getExternalId());
        assertEquals(recordFromDataBase.getJobId(), record.getJobId());
    }

    @Test
    public void shouldUpdateMessageRecord() {
        String externalId = "1234";
        String jobId = "4321";
        String jobIdUpdate = "5678";

        MessageRecord record = new MessageRecord(externalId, jobId);
        allMessageRecords.addOrUpdateMessageRecord(record);
        record.setJobId(jobIdUpdate);
        allMessageRecords.addOrUpdateMessageRecord(record);
        MessageRecord recordFromDataBase = allMessageRecords.getMessageRecordByExternalId(externalId);
        assertEquals(recordFromDataBase.getExternalId(), record.getExternalId());
        assertEquals(recordFromDataBase.getJobId(), record.getJobId());
    }
}
