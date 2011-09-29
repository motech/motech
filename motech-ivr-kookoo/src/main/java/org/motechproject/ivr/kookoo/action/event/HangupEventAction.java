package org.motechproject.ivr.kookoo.action.event;

import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class HangupEventAction extends BaseEventAction {

    public HangupEventAction() {
    }

    public HangupEventAction(KookooCallDetailRecordsService kookooCallDetailRecordsService) {
        this.kookooCallDetailRecordsService = kookooCallDetailRecordsService;
    }

    @Override
    public String createResponse(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public void postHandle(String callId, IVRRequest ivrRequest, HttpServletRequest request,
                           HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        raiseDisconnectEvent(callId, ivrSession);
        ivrSession.close();
    }

    private void raiseDisconnectEvent(String callId, IVRSession ivrSession) {
        kookooCallDetailRecordsService.close(callId, ivrSession.getExternalId());
    }
}
