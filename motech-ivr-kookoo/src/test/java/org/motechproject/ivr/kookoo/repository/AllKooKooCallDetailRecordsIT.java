package org.motechproject.ivr.kookoo.repository;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Ignore;
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

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.util.DateUtil.newDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationIvrKookooBundle.xml"})
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

    @Ignore // TODO
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

    @Ignore // TODO
    @Test
    public void shouldFetchCallLogsWithinADateRange() {
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 2), "1");
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 3), "2");
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 4), "3");
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 5), "4");
        List<KookooCallDetailRecord> result = allKooKooCallDetailRecords.findByStartDate(newDateTime(2012, 10, 3), newDateTime(2012, 10, 4), 10);
        assertEquals(asList(new String[]{ "2", "3" }), extract(result, on(KookooCallDetailRecord.class).getVendorCallId()));
    }

    @Ignore // TODO
    @Test
    public void shouldDeleteCallLogsWithinADateRange() {
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 2), "1");
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 3), "2");
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 4), "3");
        createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 5), "4");
        allKooKooCallDetailRecords.removeInRange(newDateTime(2012, 10, 3), newDateTime(2012, 10, 5));
        assertEquals(1, allKooKooCallDetailRecords.getAll().size());
        assertEquals("1", allKooKooCallDetailRecords.getAll().get(0).getVendorCallId());
    }

    @Ignore // TODO
    @Test
    public void shouldDeleteCallLogsInBatches() {
        for (int i = 0; i < 10; i++)
            createKookooCallDetailRecord("phoneNumber", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, i + 1, 6, 0, 0), String.valueOf(i));
        allKooKooCallDetailRecords.removeInRange(newDateTime(2012, 10, 1, 6, 0, 0), newDateTime(2012, 10, 7, 6, 0, 0), 3);
        assertEquals(3, allKooKooCallDetailRecords.getAll().size());
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
