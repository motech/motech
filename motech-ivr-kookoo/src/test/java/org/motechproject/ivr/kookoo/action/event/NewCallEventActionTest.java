package org.motechproject.ivr.kookoo.action.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.action.UserNotFoundAction;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.kookoo.service.UserService;
import org.motechproject.server.service.ivr.IVRCallState;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;


public class NewCallEventActionTest extends BaseActionTest {

    private NewCallEventAction action;
    @Mock
    private UserService userService;
    @Mock
    private IVRMessage ivrMessages;
    @Mock
    private UserNotFoundAction userNotFoundAction;
    @Mock
    private KookooCallDetailRecordsService kookooCallDetailRecordsService;

    @Before
    public void setUp() {
        super.setUp();
        action = new NewCallEventAction(ivrMessages, userNotFoundAction, userService, kookooCallDetailRecordsService);
    }

    @Test
    public void shouldAskUserNotFoundActionToHandleIfUserIsNotRegistered() {
        IVRRequest ivrRequest = new KookooRequest();
        when(userService.isRegisteredUser(ivrRequest.getCid())).thenReturn(false);

        action.createResponse(ivrRequest, request, response);
        verify(userNotFoundAction).createResponse(ivrRequest, request, response);
    }

    @Test
    public void shouldSetAttributesInSessionAndSendDtmfResponseWithWav() {
        IVRRequest ivrRequest = new KookooRequest();
        when(userService.isRegisteredUser(ivrRequest.getCid())).thenReturn(true);
        when(request.getSession()).thenReturn(session);

        String xmlResponse = action.createResponse(ivrRequest, request, response);
        verify(session).setAttribute(IVRSession.IVRCallAttribute.CALLER_ID, ivrRequest.getCid());
        verify(session).setAttribute(IVRSession.IVRCallAttribute.CALL_STATE, IVRCallState.COLLECT_PIN);
        assertEquals("<response><collectdtmf><playaudio/></collectdtmf></response>", sanitize(xmlResponse));
    }
}
