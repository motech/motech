package org.motechproject.ivr.action.event;

import org.apache.commons.lang.StringUtils;
import org.motechproject.eventtracking.domain.Event;
import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.action.BaseAction;
import org.motechproject.ivr.eventlogging.EventDataBuilder;
import org.motechproject.server.service.ivr.IVRCallIdentifiers;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseEventAction extends BaseAction {
    @Autowired
    protected EventService eventService;
    @Autowired
    protected IVRCallIdentifiers ivrCallIdentifiers;

    Map<String, String> eventData = new HashMap<String, String>();

    public BaseEventAction() {
    }

    public BaseEventAction(EventService eventService, IVRCallIdentifiers ivrCallIdentifiers) {
        this.eventService = eventService;
        this.ivrCallIdentifiers = ivrCallIdentifiers;
    }

    public String handleInternal(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String responseXML = handle(ivrRequest, request, response);
        publishCallEvent(ivrRequest, request, responseXML);
        postHandle(ivrRequest, request, response);
        return responseXML;
    }

    private void publishCallEvent(IVRRequest ivrRequest, HttpServletRequest request, String responseXML) {
        IVRSession ivrSession = getIVRSession(request);
        String externalId = getExternalId(ivrSession);
        String callId = getCallId(ivrRequest, ivrSession);
        Map<String, String> requestParams = getParams(request);

        EventDataBuilder builder = new EventDataBuilder(callId, externalId, ivrRequest.callEvent().toString(), requestParams, DateUtil.now());
        Event callEvent = builder.withCallerId(ivrRequest.getCallerId())
                .withCallDirection(ivrRequest.getCallDirection())
                .withResponseXML(responseXML)
                .withData(eventData).build();
        eventService.publishEvent(callEvent);
    }

    private String getExternalId(IVRSession ivrSession) {
        return ivrSession.sessionExists() ? ivrSession.getExternalId() : "Unknown";
    }

    private String getCallId(IVRRequest ivrRequest, IVRSession ivrSession) {
        if (ivrSession.sessionExists() && StringUtils.isNotBlank(ivrSession.getCallId()))
            return ivrSession.getCallId();
        String callId = StringUtils.isNotBlank(ivrRequest.getCallId()) ? ivrRequest.getCallId() : ivrCallIdentifiers.getNew();
        ivrSession.set(IVRSession.IVRCallAttribute.CALL_ID, callId);
        return ivrSession.getCallId();
    }

    private Map<String, String> getParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String[]> e : (Set<Map.Entry<String, String[]>>) request.getParameterMap().entrySet())
            params.put(e.getKey(), e.getValue()[0]);
        return params;
    }

    public void postHandle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return;
    }

    protected void addEventLogData(String key, String value) {
        eventData.put(key, value);
    }


}
