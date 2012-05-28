package org.motechproject.ivr.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.domain.CallSessionRecord;
import org.motechproject.ivr.repository.AllCallSessionRecords;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class IVRSessionManagementServiceImplTest {

    @Mock
    private AllCallSessionRecords allCallSessionRecords;
    private IVRSessionManagementServiceImpl ivrSessionManagementService;

    @Before
    public void setUp() {
        initMocks(this);
        ivrSessionManagementService = new IVRSessionManagementServiceImpl(allCallSessionRecords);
    }

    @Test
    public void shouldGetACallSessionRecordBasedOnSessionId() {
        ivrSessionManagementService.getCallSession("session1");

        verify(allCallSessionRecords, times(1)).findOrCreate("session1");
    }

    @Test
    public void shouldUpdateAnExistingSessionRecord()  {
        CallSessionRecord callSessionRecord = mock(CallSessionRecord.class);
        ivrSessionManagementService.updateCallSession(callSessionRecord);

        verify(allCallSessionRecords, times(1)).update(callSessionRecord);
    }

    @Test
    public void shouldRemoveAnExistingSessionRecordBasedOnSessionId()  {
        CallSessionRecord callSessionRecord = new CallSessionRecord("session1");
        when(allCallSessionRecords.findBySessionId("session1")).thenReturn(callSessionRecord);

        ivrSessionManagementService.removeCallSession("session1");

        verify(allCallSessionRecords, times(1)).remove(callSessionRecord);
    }

    @Test
    public void shouldNotRemoveASessionIfNotFound()  {
        when(allCallSessionRecords.findBySessionId("session1")).thenReturn(null);

        ivrSessionManagementService.removeCallSession("session1");

        verify(allCallSessionRecords, times(0)).remove(any(CallSessionRecord.class));
    }

    @Test
    public void shouldFigureOutWhetherASessionIsValidOrNot() {
        when(allCallSessionRecords.findBySessionId("session1")).thenReturn(null);
        assertThat(ivrSessionManagementService.isValidSession("session1"), is(false));

        when(allCallSessionRecords.findBySessionId("session2")).thenReturn(new CallSessionRecord("session2"));
        assertThat(ivrSessionManagementService.isValidSession("session2"), is(true));
    }
}
