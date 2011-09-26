package org.motechproject.ivr.kookoo.action.event;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.kookoo.action.BaseAction;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.server.service.ivr.IVRCallIdentifiers;
import org.motechproject.server.service.ivr.IVRRequest;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseEventAction extends BaseAction {

    @Autowired
    protected EventService eventService;

    @Autowired
    protected IVRCallIdentifiers ivrCallIdentifiers;

    @Autowired
    private AllKooKooCallDetailRecords allKooKooCallDetailRecords;

    private Map<String, String> callEventData = new HashMap<String, String>();

    public BaseEventAction() {
    }

    public BaseEventAction(EventService eventService, IVRCallIdentifiers ivrCallIdentifiers,
                           AllKooKooCallDetailRecords allKooKooCallDetailRecords) {
        this.eventService = eventService;
        this.ivrCallIdentifiers = ivrCallIdentifiers;
        this.allKooKooCallDetailRecords = allKooKooCallDetailRecords;
    }

    public String handleInternal(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String responseXML = handle(ivrRequest, request, response);
        publishCallEvent(ivrRequest, request, responseXML);
        postHandle(ivrRequest, request, response);
        return responseXML;
    }

    public void postHandle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return;
    }

    protected void addCallEventData(String key, String value) {
        callEventData.put(key, value);
    }

    private void publishCallEvent(IVRRequest ivrRequest, HttpServletRequest request, String responseXML) {
        addCallEventData(CallEventConstants.RESPONSE_XML, responseXML);
        CallEvent callEvent = new CallEvent(ivrRequest.callEvent().key(), callEventData);
        updateCallDetailRecord(ivrRequest.getSid(), callEvent);
    }

    private void updateCallDetailRecord(String callId, CallEvent callEvent) {
        KookooCallDetailRecord callDetailRecord = allKooKooCallDetailRecords.findByCallId(callId);
        callDetailRecord.addCallEvent(callEvent);
        allKooKooCallDetailRecords.update(callDetailRecord);
    }
}
