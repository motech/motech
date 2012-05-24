package org.motechproject.ivr.kookoo.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.Options;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.model.CallDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.motechproject.util.DateUtil.newDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ivrKookooRepositories.xml"})
public class AllKooKooCallDetailRecordsIT {

    @Autowired
    private AllKooKooCallDetailRecords allKooKooCallDetailRecords;

    @Autowired
    @Qualifier("kookooIvrDbConnector")
    private CouchDbConnector ivrKookooCouchDbConnector;

    @After
    public void teardown() {
        allKooKooCallDetailRecords.removeAll();
    }

    @Test
    public void shouldFindCallDetailRecordByCallId() {
        CallDetailRecord callDetailRecord = CallDetailRecord.create("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED);
        CallEvent callEvent = new CallEvent(IVREvent.GotDTMF.toString());
        callEvent.appendData(IVREvent.GotDTMF.toString(), "1234");
        callDetailRecord.addCallEvent(callEvent);
        KookooCallDetailRecord kookooCallDetailRecord = new KookooCallDetailRecord(callDetailRecord, "dfdsfds");
        allKooKooCallDetailRecords.add(kookooCallDetailRecord);
        KookooCallDetailRecord result = allKooKooCallDetailRecords.get(kookooCallDetailRecord.getId());
        assertNotNull(result);
        ivrKookooCouchDbConnector.delete(kookooCallDetailRecord);
    }

    @Test
    public void shouldFetchCallLogsWithinADateRange() {
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 2), "1");
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 3), "2");
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 4), "3");
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 5), "4");
        List<KookooCallDetailRecord> result = allKooKooCallDetailRecords.findByStartDate(newDateTime(2012, 10, 3), newDateTime(2012, 10, 4), 10);
        assertEquals(asList(new String[]{ "2", "3" }), extract(result, on(KookooCallDetailRecord.class).getVendorCallId()));
    }

    @Test
    public void shouldDeleteCallLogsWithinADateRange() {
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 2), "1");
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 3), "2");
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 4), "3");
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 5), "4");
        allKooKooCallDetailRecords.remove(newDateTime(2012, 10, 3), newDateTime(2012, 10, 5));
        assertEquals(1, allKooKooCallDetailRecords.getAll().size());
        assertEquals("1", allKooKooCallDetailRecords.getAll().get(0).getVendorCallId());
    }

    @Test
    public void shouldDeleteCallLogsInBatches() {
        for (int i = 0; i < 10; i++)
            createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, i + 1, 6, 0, 0), String.valueOf(i));
        allKooKooCallDetailRecords.remove(newDateTime(2012, 10, 1, 6, 0, 0), newDateTime(2012, 10, 7, 6, 0, 0), 3);
        assertEquals(3, allKooKooCallDetailRecords.getAll().size());
    }

    @Test
    public void shouldPurgCallLogsWithinADateRange() {
        List<KookooCallDetailRecord> records = new ArrayList<KookooCallDetailRecord>();
        records.add(createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 2), "1"));
        records.add(createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 3), "2"));
        records.add(createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 4), "3"));
        records.add(createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 5), "4"));

        allKooKooCallDetailRecords.purge(newDateTime(2012, 10, 3), newDateTime(2012, 10, 5));

        List<KookooCallDetailRecord> dbRecords = allKooKooCallDetailRecords.getAll();
        assertEquals(1, dbRecords.size());
        assertEquals("1", dbRecords.get(0).getVendorCallId());

        List<KookooCallDetailRecord> purgedRecords = records.subList(1, records.size() - 1);
        for (KookooCallDetailRecord record : purgedRecords)
            assertDocumentAbsent(record.getId(), record.getRevision());
    }

    @Test
    public void shouldPurgCallLogsInBatches() {
        List<KookooCallDetailRecord> records = new ArrayList<KookooCallDetailRecord>();
        records.add(createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 2), "1"));
        records.add(createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 3), "2"));
        records.add(createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 4), "3"));
        records.add(createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 5), "4"));

        allKooKooCallDetailRecords.purge(newDateTime(2012, 10, 3), newDateTime(2012, 10, 5), 2);

        List<KookooCallDetailRecord> dbRecords = allKooKooCallDetailRecords.getAll();
        assertEquals(1, dbRecords.size());
        assertEquals("1", dbRecords.get(0).getVendorCallId());

        List<KookooCallDetailRecord> purgedRecords = records.subList(1, records.size() - 1);
        for (KookooCallDetailRecord record : purgedRecords)
            assertDocumentAbsent(record.getId(), record.getRevision());
    }

    private void assertDocumentAbsent(String id, String revision) {
        try {
            allKooKooCallDetailRecords.get(id, new Options().revision(revision));
        } catch (DocumentNotFoundException e) {
            return;
        }
        fail();
    }

    private KookooCallDetailRecord buildKookooCallDetailRecord(String phoneNumber, CallDirection callDirection, CallDetailRecord.Disposition disposition, DateTime startDate, String vendorCallId) {
        CallDetailRecord callDetailRecord = CallDetailRecord.create(phoneNumber, callDirection, disposition);
        callDetailRecord.setStartDate(startDate.toDate());
        KookooCallDetailRecord kookooCallDetailRecord = new KookooCallDetailRecord(callDetailRecord, vendorCallId);
        return kookooCallDetailRecord;
    }

    private KookooCallDetailRecord createKookooCallDetailRecord(String phoneNumber, CallDirection callDirection, CallDetailRecord.Disposition disposition, DateTime startDate, String vendorCallId) {
        KookooCallDetailRecord kookooCallDetailRecord = buildKookooCallDetailRecord(phoneNumber, callDirection, disposition, startDate, vendorCallId);
        allKooKooCallDetailRecords.add(kookooCallDetailRecord);
        return kookooCallDetailRecord;
    }
}
