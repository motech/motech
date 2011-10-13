package org.motechproject.ivr.kookoo.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.IVREvent;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class KookooCallDetailRecordsServiceImplTest {

    @Mock
    private AllKooKooCallDetailRecords allKooKooCallDetailRecords;

    @Mock
    private EventService eventService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldUpdateTheEndDateWhenClosingCallDetailRecord() {
        KookooCallDetailRecordsServiceImpl kookooCallDetailRecordsService = new KookooCallDetailRecordsServiceImpl(allKooKooCallDetailRecords, eventService, allKooKooCallDetailRecords);
        KookooCallDetailRecord kookooCallDetailRecord = new KookooCallDetailRecord();
        CallDetailRecord callDetailRecord = mock(CallDetailRecord.class);
        kookooCallDetailRecord.setCallDetailRecord(callDetailRecord);

        when(allKooKooCallDetailRecords.get("callId")).thenReturn(kookooCallDetailRecord);

        kookooCallDetailRecordsService.close("callId", "externalId", IVREvent.GotDTMF.toString());

        verify(callDetailRecord).setEndDate(Matchers.<Date>any());
        verify(allKooKooCallDetailRecords).update(kookooCallDetailRecord);
    }
}
