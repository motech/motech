package org.motechproject.decisiontree.server.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.server.domain.FlowSessionRecord;
import org.motechproject.decisiontree.server.repository.AllFlowSessionRecords;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IVRSessionManagementServiceImplTest {

    @Mock
    private AllFlowSessionRecords allFlowSessionRecords;
    private FlowSessionServiceImpl ivrSessionManagementService;

    @Before
    public void setUp() {
        initMocks(this);
        ivrSessionManagementService = new FlowSessionServiceImpl(allFlowSessionRecords);
    }

    @Test
    public void shouldUpdateAnExistingSessionRecord()  {
        FlowSessionRecord flowSessionRecord = new FlowSessionRecord("foo", "1234567890");
        ivrSessionManagementService.updateSession(flowSessionRecord);
        verify(allFlowSessionRecords, times(1)).update(flowSessionRecord);
    }

    @Test
    public void shouldRemoveAnExistingSessionRecordBasedOnSessionId()  {
        FlowSessionRecord flowSessionRecord = new FlowSessionRecord("session1", "1234567890");
        when(allFlowSessionRecords.findBySessionId("session1")).thenReturn(flowSessionRecord);

        ivrSessionManagementService.removeCallSession("session1");

        verify(allFlowSessionRecords, times(1)).remove(flowSessionRecord);
    }

    @Test
    public void shouldNotRemoveASessionIfNotFound()  {
        when(allFlowSessionRecords.findBySessionId("session1")).thenReturn(null);

        ivrSessionManagementService.removeCallSession("session1");

        verify(allFlowSessionRecords, times(0)).remove(any(FlowSessionRecord.class));
    }

    @Test
    public void shouldFigureOutWhetherASessionIsValidOrNot() {
        when(allFlowSessionRecords.findBySessionId("session1")).thenReturn(null);
        assertThat(ivrSessionManagementService.isValidSession("session1"), is(false));

        when(allFlowSessionRecords.findBySessionId("session2")).thenReturn(new FlowSessionRecord("session2", "1234567890"));
        assertThat(ivrSessionManagementService.isValidSession("session2"), is(true));
    }
}
