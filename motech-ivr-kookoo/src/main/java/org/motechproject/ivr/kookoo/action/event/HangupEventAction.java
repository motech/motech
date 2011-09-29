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
public class HangupEventAction extends BaseEventAction {

    public HangupEventAction() {
    }

    public HangupEventAction(EventService eventService, KookooCallDetailRecordsService kookooCallDetailRecordsService) {
        this.eventService = eventService;
        this.kookooCallDetailRecordsService = kookooCallDetailRecordsService;
    }

    @Override
    public String createResponse(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public void postHandle(IVRRequest ivrRequest, HttpServletRequest request,
                           HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        raiseDisconnectEvent(ivrSession, request);
        ivrSession.close();
    }

    private void raiseDisconnectEvent(IVRSession ivrSession, HttpServletRequest request) {
        String callId = getCallIdFromCookie(request);
        kookooCallDetailRecordsService.findByCallId(callId).close();
        eventService.publishEvent(new EndOfCallEvent(callId, ivrSession.getExternalId()));
    }
}
