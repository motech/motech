package org.motechproject.ivr.action.event;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.action.BaseAction;
import org.motechproject.ivr.eventlogging.EventDataBuilder;
import org.motechproject.server.service.ivr.IVREvent;
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

    Map<String, String> eventData = new HashMap<String, String>();

    protected void publishIVREvent(String sessionId, IVREvent name, String externalId, Map<String, String> requestParams, IVRRequest.CallDirection callDirection, String callerId, String inputData, String responseXML) {
        EventDataBuilder builder = new EventDataBuilder(sessionId, name.toString(), externalId, requestParams, DateUtil.now());
        builder.withResponseXML(responseXML)
                .withCallDirection(callDirection)
                .withCallerId(callerId)
                .withData(eventData);
        eventService.publishEvent(builder.build());
    }

    public String handleInternal(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String responseXML = handle(ivrRequest, request, response);
        IVRSession ivrSession = getIVRSession(request);
        String patientId = ivrSession.isValid() ? ivrSession.getExternalId() : "Unknown";
        Map<String, String> requestParams = convertParams(request.getParameterMap());
        publishIVREvent(ivrRequest.getSessionId(), ivrRequest.callEvent(), patientId, requestParams, ivrRequest.getCallDirection(), ivrRequest.getCallerId(), ivrRequest.getData(), responseXML);
        postHandle(ivrRequest, request, response);
        return responseXML;
    }

    private Map<String, String> convertParams(Map requestParameterMap) {
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String[]> e : (Set<Map.Entry<String, String[]>>) requestParameterMap.entrySet()) {
            params.put(e.getKey(), e.getValue()[0]);
        }
        return params;
    }

    public void postHandle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return;
    }

    protected void addEventLogData(String key, String value) {
        eventData.put(key, value);
    }
}
