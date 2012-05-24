package org.motechproject.ivr.kookoo.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.model.CallDirection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.newDateTime;

public class AllKooKooCallDetailRecordsTest {

    @Mock
    private CouchDbConnector db;

    List<KookooCallDetailRecord> sampleRecords;

    DateTime someStartDate;
    DateTime someEndDate;

    @Before
    public void setup() {
        initMocks(this);
        sampleRecords = new ArrayList<KookooCallDetailRecord>();
        sampleRecords.add(buildKookooCallDetailRecord("1234567890", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 1), "1", "1"));
        sampleRecords.add(buildKookooCallDetailRecord("1234567890", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 2), "2", "2"));
        sampleRecords.add(buildKookooCallDetailRecord("1234567890", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 3), "3", "3"));
        sampleRecords.add(buildKookooCallDetailRecord("1234567890", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 4), "4", "4"));
        sampleRecords.add(buildKookooCallDetailRecord("1234567890", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 5), "5", "5"));
        sampleRecords.add(buildKookooCallDetailRecord("1234567890", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 6), "6", "6"));
        sampleRecords.add(buildKookooCallDetailRecord("1234567890", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED, newDateTime(2012, 10, 7), "7", "7"));

        // values are irrelevant, repoitory is mocked
        someStartDate = newDateTime(2012, 10, 1, 0, 0, 0);
        someEndDate = newDateTime(2012, 10, 1, 0, 0, 0);
    }

    @Test
    public void shouldDeleteDocumentsIn1Batch() {
        when(db.queryView(any(ViewQuery.class), any(Class.class))).thenReturn(sampleRecords).thenReturn(new ArrayList());

        AllKooKooCallDetailRecords allKooKooCallDetailRecords = new AllKooKooCallDetailRecords(db);

        allKooKooCallDetailRecords.remove(someStartDate, someEndDate, 10);

        ArgumentCaptor batchCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(db).executeBulk((Collection<?>) batchCaptor.capture());

        List<KookooCallDetailRecord> batchedDocuments = (List<KookooCallDetailRecord>) batchCaptor.getValue();
        assertEquals(7, batchedDocuments.size());
    }

    @Test
    public void shouldDeleteDocumentsIn2Batches() {
        int batchSize = 4;
        when(db.queryView(any(ViewQuery.class), any(Class.class))).thenReturn(sampleRecords.subList(0, batchSize)).thenReturn(sampleRecords.subList(batchSize, sampleRecords.size())).thenReturn(new ArrayList());

        AllKooKooCallDetailRecords allKooKooCallDetailRecords = new AllKooKooCallDetailRecords(db);
        allKooKooCallDetailRecords.remove(someStartDate, someEndDate, batchSize);

        ArgumentCaptor batchCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(db, times(2)).executeBulk((Collection<?>) batchCaptor.capture());

        List batches = batchCaptor.getAllValues();
        assertEquals(2, batches.size());

        List<KookooCallDetailRecord> firstBatch = (List<KookooCallDetailRecord>) batches.get(0);
        List<KookooCallDetailRecord> secondBatch = (List<KookooCallDetailRecord>) batches.get(1);
        assertEquals(4, firstBatch.size());
        assertEquals(3, secondBatch.size());
    }

    @Test
    public void shouldPurgeDocumentsIn1Batch() {
        when(db.queryView(any(ViewQuery.class), any(Class.class))).thenReturn(sampleRecords).thenReturn(new ArrayList());

        AllKooKooCallDetailRecords allKooKooCallDetailRecords = new AllKooKooCallDetailRecords(db);
        allKooKooCallDetailRecords.purge(someStartDate, someEndDate, 10);

        ArgumentCaptor purgeRequestCaptor = ArgumentCaptor.forClass(Map.class);
        verify(db).purge((Map<String, List<String>>) purgeRequestCaptor.capture());

        Map<String, List<String>> purgeRequest = (Map<String, List<String>>) purgeRequestCaptor.getValue();
        assertEquals(7, purgeRequest.keySet().size());
    }

    @Test
    public void shouldPurgeDocumentsIn2Batches() {
        int batchSize = 4;
        when(db.queryView(any(ViewQuery.class), any(Class.class))).thenReturn(sampleRecords.subList(0, batchSize)).thenReturn(sampleRecords.subList(batchSize, sampleRecords.size())).thenReturn(new ArrayList());

        AllKooKooCallDetailRecords allKooKooCallDetailRecords = new AllKooKooCallDetailRecords(db);
        allKooKooCallDetailRecords.purge(someStartDate, someEndDate, batchSize);

        ArgumentCaptor purgeRequestCaptor = ArgumentCaptor.forClass(Map.class);
        verify(db, times(2)).purge((Map<String, List<String>>) purgeRequestCaptor.capture());

        List batches = purgeRequestCaptor.getAllValues();
        assertEquals(2, batches.size());

        Map<String, List<String>> firstBatch = (Map<String, List<String>>) batches.get(0);
        Map<String, List<String>> secondBatch = (Map<String, List<String>>) batches.get(1);
        assertEquals(4, firstBatch.keySet().size());
        assertEquals(3, secondBatch.keySet().size());
    }

    private KookooCallDetailRecord buildKookooCallDetailRecord(String phoneNumber, CallDirection callDirection, CallDetailRecord.Disposition disposition, DateTime startDate, String vendorCallId, String id) {
        CallDetailRecord callDetailRecord = CallDetailRecord.create(phoneNumber, callDirection, disposition);
        callDetailRecord.setStartDate(startDate.toDate());
        KookooCallDetailRecord kookooCallDetailRecord = new KookooCallDetailRecord(callDetailRecord, vendorCallId);
        kookooCallDetailRecord.setId(id);
        return kookooCallDetailRecord;
    }
}
