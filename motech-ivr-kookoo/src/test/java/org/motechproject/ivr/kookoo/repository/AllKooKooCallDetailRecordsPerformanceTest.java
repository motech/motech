package org.motechproject.ivr.kookoo.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.model.CallDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.lang.String.format;
import static org.motechproject.util.DateUtil.newDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationIvrKookooBundle.xml"})
public class AllKooKooCallDetailRecordsPerformanceTest {

    @Autowired
    private AllKooKooCallDetailRecords allKooKooCallDetailRecords;

    @Autowired
    @Qualifier("kookooIvrDbConnector")
    private CouchDbConnector ivrKookooCouchDbConnector;

    @Autowired
    CouchDbInstance couchDbInstance;

    @After
    public void teardown() {
        couchDbInstance.deleteDatabase(ivrKookooCouchDbConnector.getDatabaseName());
    }

    @Test
    @Ignore("performance test to measure time; unignore to run")
    public void printsTimeTakenToDeleteLargeNumberOfDocuments() {
        Random r = new Random();
        int numberOfRecords = 100000;
        int batchSize = 1000;

        for (int i = 0; i < numberOfRecords / batchSize; i++) {   // rudimentary logic, use perfectly divisible values
            List<KookooCallDetailRecord> records = new ArrayList<KookooCallDetailRecord>();
            for (int j = 0; j < batchSize; j++)
                records.add(buildKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2000 + r.nextInt(10), 1 + r.nextInt(11), 1 + r.nextInt(27)), UUID.randomUUID().toString()));
            ivrKookooCouchDbConnector.executeBulk(records);
        }
        forceViewIndex();

        System.out.println(format("inserted %d records", numberOfRecords));

        long startTime = System.currentTimeMillis();
        allKooKooCallDetailRecords.removeInRange(newDateTime(2000, 1, 1), newDateTime(2010, 1, 1), 5000);
        System.out.println(format("deleted in %ds", (System.currentTimeMillis() - startTime) / 1000));
    }

    private void forceViewIndex() {
        try {
            allKooKooCallDetailRecords.getAll();
        } catch (Exception e) { // catching timout; hope index will be built soon
            try { Thread.sleep(10000); } catch (InterruptedException e1) { }
        }
    }

    private KookooCallDetailRecord buildKookooCallDetailRecord(String phoneNumber, CallDirection callDirection, CallDetailRecord.Disposition disposition, DateTime startDate, String vendorCallId) {
        CallDetailRecord callDetailRecord = CallDetailRecord.create(phoneNumber, callDirection, disposition);
        callDetailRecord.setStartDate(startDate.toDate());
        KookooCallDetailRecord kookooCallDetailRecord = new KookooCallDetailRecord(callDetailRecord, vendorCallId);
        return kookooCallDetailRecord;
    }
}
