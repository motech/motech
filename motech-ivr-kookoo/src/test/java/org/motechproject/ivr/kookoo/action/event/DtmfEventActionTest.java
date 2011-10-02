package org.motechproject.ivr.kookoo.action.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.ivr.kookoo.action.AuthenticateAction;
import org.motechproject.server.service.ivr.IVRCallState;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession.IVRCallAttribute;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DtmfEventActionTest extends BaseActionTest {
    private DtmfEventAction eventAction;
    @Mock
    private AuthenticateAction authenticateAction;

    @Mock
    IVRRequest ivrRequest;

    @Before
    public void setUp() {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        eventAction = new DtmfEventAction(authenticateAction);
    }

    @Test
    public void shouldDelegateToAuthenticateActionIfCollectPin() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.CALL_STATE)).thenReturn(IVRCallState.COLLECT_PIN);

        String handle = eventAction.handle("callId", ivrRequest, request, response);

        verify(authenticateAction).handle("callId", ivrRequest, request, response);
    }
}
