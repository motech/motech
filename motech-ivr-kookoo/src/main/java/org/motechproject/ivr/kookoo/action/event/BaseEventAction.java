package org.motechproject.ivr.kookoo.action.event;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.kookoo.action.BaseAction;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.CallEvent;
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
    protected KookooCallDetailRecordsService kookooCallDetailRecordsService;

    private Map<String, String> callEventData = new HashMap<String, String>();

    public BaseEventAction() {
    }

    public BaseEventAction(KookooCallDetailRecordsService kookooCallDetailRecordsService) {
        this.kookooCallDetailRecordsService = kookooCallDetailRecordsService;
    }

    public String handleInternal(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String responseXML = handle(ivrRequest, request, response);
        publishCallEvent(ivrRequest, request, responseXML);
        postHandle(ivrRequest, request, response);
        return responseXML;
    }

    public void postHandle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
    }

    protected void addCallEventData(String key, String value) {
        callEventData.put(key, value);
    }

    private void publishCallEvent(IVRRequest ivrRequest, HttpServletRequest request, String responseXML) {
        addCallEventData(CallEventConstants.RESPONSE_XML, responseXML);
        CallEvent callEvent = new CallEvent(ivrRequest.callEvent().key(), callEventData);
        kookooCallDetailRecordsService.appendEvent(ivrRequest.getSid(), callEvent);
    }
}
