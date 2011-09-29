package org.motechproject.ivr.kookoo.action.event;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.kookoo.EndOfCallEvent;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class DisconnectEventAction extends BaseEventAction {

    public DisconnectEventAction() {
    }

    public DisconnectEventAction(EventService eventService, KookooCallDetailRecordsService kookooCallDetailRecordsService) {
        this.eventService = eventService;
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
        kookooCallDetailRecordsService.findByCallId(callId).close();
        eventService.publishEvent(new EndOfCallEvent(callId, ivrSession.getExternalId()));
    }
}
