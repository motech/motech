package org.motechproject.ivr.kookoo.action.event;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.kookoo.EndOfCallEvent;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
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
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public void postHandle(IVRRequest ivrRequest, HttpServletRequest request,
                           HttpServletResponse response) {
        String callId = ivrRequest.getSid();
        KookooCallDetailRecord callDetailRecord = kookooCallDetailRecordsService.findByCallId(callId);
        callDetailRecord.close();
        raiseDisconnectEvent(getIVRSession(request), callDetailRecord);
        getIVRSession(request).close();
    }

    private void raiseDisconnectEvent(IVRSession ivrSession, KookooCallDetailRecord kookooCallDetailRecord) {
        String callId = kookooCallDetailRecord.getCallDetailRecord().getCallId();
        eventService.publishEvent(new EndOfCallEvent(callId, ivrSession.getExternalId()));
    }
}
