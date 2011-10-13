package org.motechproject.ivr.kookoo.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.HangupException;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KooKooIVRContextForTest;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.IVREvent;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SafeIVRControllerTest {
    @Mock
    private KookooCallDetailRecordsService callDetailRecordsService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void respondWithHangupWhenHangupExceptionHappens() {
        SafeIVRController safeIVRController = new SafeIVRControllerForTest(null, callDetailRecordsService);
        KooKooIVRContextForTest ivrContext = new KooKooIVRContextForTest().externalId("9854").ivrEvent(IVREvent.GotDTMF);
        ivrContext.callDetailRecordId("12345");
        safeIVRController.safeCall(ivrContext);
        verify(callDetailRecordsService).close(ivrContext.callDetailRecordId(), ivrContext.externalId(), IVREvent.Hangup.toString());
    }

    class SafeIVRControllerForTest extends SafeIVRController {
        public SafeIVRControllerForTest(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService) {
            super(ivrMessage, callDetailRecordsService);
        }

        @Override
        public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) throws HangupException {
            throw new HangupException("");
        }
    }
}
