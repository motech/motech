package org.motechproject.ivr.kookoo.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContextForTest;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.decisiontree.service.FlowSessionService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class StandardResponseControllerTest {
    @Mock
    private KookooCallDetailRecordsService kookooCallDetailsRecordsService;
    @Mock
    private FlowSessionService flowSessionService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldDeleteSessionEntryAfterHangingUp() {
        StandardResponseController controller = new StandardResponseController(kookooCallDetailsRecordsService, flowSessionService);
        KooKooIVRContextForTest ivrContext = new KooKooIVRContextForTest();
        ivrContext.callId("sessionId");
        controller.hangup(ivrContext);

        verify(flowSessionService).removeCallSession("sessionId");
    }
}
