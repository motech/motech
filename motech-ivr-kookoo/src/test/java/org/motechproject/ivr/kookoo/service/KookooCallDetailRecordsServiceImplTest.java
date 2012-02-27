package org.motechproject.ivr.kookoo.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.context.EventContext;
import org.motechproject.event.EventRelay;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.MotechEvent;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(EventContext.class)
public class KookooCallDetailRecordsServiceImplTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private AllKooKooCallDetailRecords allKooKooCallDetailRecords;
    @Mock
    private EventContext eventContext;
    @Mock
    private EventRelay eventRelay;

    private KookooCallDetailRecordsServiceImpl kookooCallDetailRecordsService;
    private KookooCallDetailRecord kookooCallDetailRecord;

    private final String callDetailRecordId = "callId";

    @Before
    public void setUp() {
        initMocks(this);

        PowerMockito.mockStatic(EventContext.class);
        when(EventContext.getInstance()).thenReturn(eventContext);
        when(eventContext.getEventRelay()).thenReturn(eventRelay);

        kookooCallDetailRecordsService = new KookooCallDetailRecordsServiceImpl(allKooKooCallDetailRecords, allKooKooCallDetailRecords);
        CallDetailRecord callDetailRecord = CallDetailRecord.create("85437", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED);
        kookooCallDetailRecord = new KookooCallDetailRecord(callDetailRecord, "fdsfdsf");
        when(allKooKooCallDetailRecords.get(callDetailRecordId)).thenReturn(kookooCallDetailRecord);
    }

    @Test
    public void shouldUpdateTheEndDateWhenClosingCallDetailRecord() {
        kookooCallDetailRecordsService.close("callId", "externalId", new CallEvent(IVREvent.GotDTMF.toString()));
        verify(allKooKooCallDetailRecords).update(kookooCallDetailRecord);
    }

    @Test
    public void shouldRaiseEventWhenClosingCallDetailRecord() {
        kookooCallDetailRecordsService.close("callId", "externalId", new CallEvent(IVREvent.GotDTMF.toString()));

        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(eventCaptor.capture());

        MotechEvent event = eventCaptor.getValue();
        assertEquals(KookooCallDetailRecordsServiceImpl.CLOSE_CALL_SUBJECT, event.getSubject());
        assertEquals("callId", event.getParameters().get(KookooCallDetailRecordsServiceImpl.CALL_ID));
        assertEquals("externalId", event.getParameters().get(KookooCallDetailRecordsServiceImpl.EXTERNAL_ID));
    }

    @Test
    public void appendEvent() {
        assertEquals(0, kookooCallDetailRecord.getCallDetailRecord().getCallEvents().size());
        kookooCallDetailRecordsService.appendEvent(callDetailRecordId, IVREvent.NewCall, null);
        assertEquals(1, kookooCallDetailRecord.getCallDetailRecord().getCallEvents().size());
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
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(CallEventConstants.CUSTOM_DATA_LIST, response);

        kookooCallDetailRecordsService.appendToLastCallEvent(callDetailRecordId, map);
        List<CallEvent> callEvents = kookooCallDetailRecord.getCallDetailRecord().getCallEvents();
        assertEquals(1, callEvents.size());
        assertEquals(response, callEvents.get(0).getData().getFirst(CallEventConstants.CUSTOM_DATA_LIST));
    }
   
    @Test
    public void scenario1() {
        kookooCallDetailRecordsService.appendEvent(callDetailRecordId, IVREvent.GotDTMF, "1");
        String response = "asada";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(CallEventConstants.CUSTOM_DATA_LIST, response);
        kookooCallDetailRecordsService.appendToLastCallEvent(callDetailRecordId, map);
        List<CallEvent> callEvents = kookooCallDetailRecord.getCallDetailRecord().getCallEvents();
        assertEquals(1, callEvents.size());
        assertEquals(response, callEvents.get(0).getData().getFirst(CallEventConstants.CUSTOM_DATA_LIST));
    }

    @Test
    public void create() {
        kookooCallDetailRecordsService.createAnsweredRecord("1111", "2222", CallDirection.Inbound);
        ArgumentCaptor<KookooCallDetailRecord> capture = ArgumentCaptor.forClass(KookooCallDetailRecord.class);
        verify(allKooKooCallDetailRecords).add(capture.capture());
        KookooCallDetailRecord newKookooCallDetailRecord = capture.getValue();
        assertEquals(1, newKookooCallDetailRecord.getCallDetailRecord().getCallEvents().size());
    }
}
