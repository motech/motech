package org.motechproject.ivr.kookoo.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.server.service.ivr.IVREvent;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class KookooCallDetailRecordsServiceImplTest {
    @Mock
    private AllKooKooCallDetailRecords allKooKooCallDetailRecords;
    @Mock
    private EventService eventService;
    private KookooCallDetailRecordsServiceImpl kookooCallDetailRecordsService;
    private KookooCallDetailRecord kookooCallDetailRecord;

    private final String callDetailRecordId = "callId";

    @Before
    public void setUp() {
        initMocks(this);
        kookooCallDetailRecordsService = new KookooCallDetailRecordsServiceImpl(allKooKooCallDetailRecords, eventService, allKooKooCallDetailRecords);
        CallDetailRecord callDetailRecord = CallDetailRecord.newIncomingCallRecord("85437");
        kookooCallDetailRecord = new KookooCallDetailRecord(callDetailRecord, "fdsfdsf");
        when(allKooKooCallDetailRecords.get(callDetailRecordId)).thenReturn(kookooCallDetailRecord);
    }

    @Test
    public void shouldUpdateTheEndDateWhenClosingCallDetailRecord() {
        kookooCallDetailRecordsService.close("callId", "externalId", IVREvent.GotDTMF);
        verify(allKooKooCallDetailRecords).update(kookooCallDetailRecord);
    }
    
    @Test
    public void appendEvent() {
        assertEquals(0, kookooCallDetailRecord.getCallDetailRecord().getCallEvents().size());
        kookooCallDetailRecordsService.appendEvent(callDetailRecordId, IVREvent.NewCall, null);
        assertEquals(1, kookooCallDetailRecord.getCallDetailRecord().getCallEvents().size());
    }
    
    @Test
    public void appendEventShouldNotAddTheEventIfUserInput_IsEmpty() {
        kookooCallDetailRecordsService.appendEvent(callDetailRecordId, IVREvent.GotDTMF, "");
        assertEquals(0, kookooCallDetailRecord.getCallDetailRecord().getCallEvents().size());
    }

    @Test
    public void appendEventShouldAddTheEventIfUserInput_IsNotEmpty() {
        String dtmfInput = "2";
        kookooCallDetailRecordsService.appendEvent(callDetailRecordId, IVREvent.GotDTMF, dtmfInput);
        List<CallEvent> callEvents = kookooCallDetailRecord.getCallDetailRecord().getCallEvents();
        assertEquals(1, callEvents.size());
        assertEquals(dtmfInput, callEvents.get(0).getData().getFirst(CallEventConstants.DTMF_DATA));
    }
    
    @Test
    public void appendLastCallEvent() {
        kookooCallDetailRecordsService.appendEvent(callDetailRecordId, IVREvent.GotDTMF, "1");
        KookooIVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();
        ivrResponseBuilder.withPlayAudios("abcd");
        String response = "gdfgfdgfdg";
        kookooCallDetailRecordsService.appendToLastCallEvent(callDetailRecordId, ivrResponseBuilder, response);
        List<CallEvent> callEvents = kookooCallDetailRecord.getCallDetailRecord().getCallEvents();
        assertEquals(1, callEvents.size());
        assertEquals(response, callEvents.get(0).getData().getFirst(CallEventConstants.CUSTOM_DATA_LIST));
    }
    
    @Test
    public void appendLastCallEventShouldNotAppendIfResponseIsEmpty() {
        kookooCallDetailRecordsService.appendEvent(callDetailRecordId, IVREvent.GotDTMF, "1");
        KookooIVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();
        String response = "asada";
        kookooCallDetailRecordsService.appendToLastCallEvent(callDetailRecordId, ivrResponseBuilder, response);
        List<CallEvent> callEvents = kookooCallDetailRecord.getCallDetailRecord().getCallEvents();
        assertEquals(1, callEvents.size());
        assertEquals(null, callEvents.get(0).getData().getAll(CallEventConstants.CUSTOM_DATA_LIST));
    }

    @Test
    public void scenario1() {
        kookooCallDetailRecordsService.appendEvent(callDetailRecordId, IVREvent.GotDTMF, "1");
        kookooCallDetailRecordsService.appendEvent(callDetailRecordId, IVREvent.GotDTMF, "");
        KookooIVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder().withPlayAudios("foo");
        String response = "asada";
        kookooCallDetailRecordsService.appendToLastCallEvent(callDetailRecordId, ivrResponseBuilder, response);
        List<CallEvent> callEvents = kookooCallDetailRecord.getCallDetailRecord().getCallEvents();
        assertEquals(1, callEvents.size());
        assertEquals(response, callEvents.get(0).getData().getFirst(CallEventConstants.CUSTOM_DATA_LIST));
    }

    @Test
    public void create() {
        kookooCallDetailRecordsService.create("1111", "2222", CallDirection.Inbound);
        ArgumentCaptor<KookooCallDetailRecord> capture = ArgumentCaptor.forClass(KookooCallDetailRecord.class);
        verify(allKooKooCallDetailRecords).add(capture.capture());
        KookooCallDetailRecord newKookooCallDetailRecord = capture.getValue();
        assertEquals(1, newKookooCallDetailRecord.getCallDetailRecord().getCallEvents().size());
    }
}
