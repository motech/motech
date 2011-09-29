package org.motechproject.ivr.kookoo.action.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.IVREvent;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class HangupEventActionTest extends BaseActionTest {

    private HangupEventAction action;

    private IVRRequest ivrRequest;

    @Mock
    private KookooCallDetailRecordsService kookooCallDetailRecordsService;

    @Before
    public void setUp() {
        super.setUp();
        ivrRequest = new KookooRequest();
        ivrRequest.setEvent(IVREvent.HANGUP.key());
        action = new HangupEventAction(kookooCallDetailRecordsService);

        when(session.getAttribute(IVRSession.IVRCallAttribute.EXTERNAL_ID)).thenReturn("externalId");
    }

    @Test
    public void shouldCloseTheRecord() {
        action.handle("callId", ivrRequest, request, response);
        verify(kookooCallDetailRecordsService).close("callId","externalId");
    }

}
