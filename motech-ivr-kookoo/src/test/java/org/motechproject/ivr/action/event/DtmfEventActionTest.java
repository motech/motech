package org.motechproject.ivr.action.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.ivr.action.AuthenticateAction;
import org.motechproject.server.service.ivr.IVRCallState;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession.IVRCallAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
        eventAction = new DtmfEventAction(authenticateAction, null, null);
    }

    @Test
    public void shouldDelegateToAuthenticateActionIfCollectPin() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.CALL_STATE)).thenReturn(IVRCallState.COLLECT_PIN);
        when(authenticateAction.handle(any(IVRRequest.class), any(HttpServletRequest.class), any(HttpServletResponse.class))).thenReturn("OK");

        String handle = eventAction.handle(ivrRequest, request, response);

        assertEquals("OK", handle);
        verify(authenticateAction).handle(ivrRequest, request, response);
    }
}
