package org.motechproject.ivr.kookoo.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KooKooIVRContextForTest;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.service.IVRSessionManagementService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class StandardResponseControllerTest {
    @Mock
    private KookooCallDetailRecordsService kookooCallDetailsRecordsService;
    @Mock
    private IVRSessionManagementService ivrSessionManagementService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldDeleteSessionEntryAfterHangingUp() {
        StandardResponseController controller = new StandardResponseController(kookooCallDetailsRecordsService, ivrSessionManagementService);
        KooKooIVRContextForTest ivrContext = new KooKooIVRContextForTest();
        ivrContext.callId("sessionId");
        controller.hangup(ivrContext);

        verify(ivrSessionManagementService).removeCallSession("sessionId");
    }
}
