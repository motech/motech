package org.motechproject.ivr.kookoo.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ivrKookooRepositories.xml"})
public class AllKooKooCallDetailRecordsIT {

    @Autowired
    private AllKooKooCallDetailRecords allKooKooCallDetailRecords;

    @Autowired
    @Qualifier("kookooIvrDbConnector")
    private CouchDbConnector ivrKookooCouchDbConnector;

    @Test
    public void shouldFindCallDetailRecordByCallId() {
        CallDetailRecord callDetailRecord = CallDetailRecord.newIncomingCallRecord("phoneNumber");
        allKooKooCallDetailRecords.add(new KookooCallDetailRecord(callDetailRecord));

        KookooCallDetailRecord kookooCallDetailRecord = allKooKooCallDetailRecords.findByCallId("callId");
        assertNotNull(kookooCallDetailRecord);

        ivrKookooCouchDbConnector.delete(kookooCallDetailRecord);
    }
}
