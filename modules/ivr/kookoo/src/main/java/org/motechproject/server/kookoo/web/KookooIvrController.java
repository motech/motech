package org.motechproject.server.kookoo.web;

import org.motechproject.callflow.domain.IvrEvent;
import org.motechproject.callflow.service.CallFlowServer;
import org.motechproject.callflow.service.FlowSessionService;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.CallStatus;
import org.motechproject.decisiontree.core.model.DialStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

@Controller
@RequestMapping("/kookoo")
public class KookooIvrController {

    public static final String STATUS = "status";

    private CallFlowServer callFlowServer;
    private FlowSessionService flowSessionService;
    private Map<String, IvrEvent> callEvents;

    @Autowired
    public KookooIvrController(CallFlowServer callFlowServer, FlowSessionService flowSessionService) {
        this.callFlowServer = callFlowServer;
        this.flowSessionService = flowSessionService;
        constructCallEvents();
    }

    @RequestMapping("/ivr")
    public ModelAndView ivrCallback(HttpServletRequest request, HttpServletResponse response) {
        String motechCallId = request.getParameter("motech_call_id");
        String kookooSid = request.getParameter("sid");
        String phoneNumber = request.getParameter("cid");

        FlowSession session = null;
        if (motechCallId == null) {
            session = flowSessionService.findOrCreate(kookooSid, phoneNumber);
        } else {
            session = updateOutgoingCallSessionIdWithKookooSid(motechCallId, kookooSid);
        }

        String transitionKey = null;
        String event = request.getParameter("event");
        String eventKey = event;
        if ("GotDTMF".equals(event)) {
            transitionKey = request.getParameter("data");
        } else if ("Hangup".equals(event)) {
            transitionKey = CallStatus.Hangup.toString();
        } else if ("Disconnect".equals(event)) {
            transitionKey = CallStatus.Disconnect.toString();
        } else if ("Dial".equals(event)) {
            transitionKey = getDialStatus(request);
            String dialStatus = request.getParameter("status");
            eventKey += "&" + dialStatus;
        }

        String tree = request.getParameter("tree");
        String language = request.getParameter("ln");

        session.setLanguage(language);
        session = setCustomParams(session, request);
        flowSessionService.updateSession(session);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        raiseCallEvent(eventKey, kookooSid);

        ModelAndView view = callFlowServer.getResponse(kookooSid, phoneNumber, "kookoo", tree, transitionKey, language);
        view.addObject("contextPath", request.getContextPath());
        view.addObject("servletPath", request.getServletPath());
        view.addObject("host", request.getHeader("Host"));
        view.addObject("scheme", request.getScheme());
        return view;
    }

    @RequestMapping(value = "/ivr/callstatus")
    @ResponseStatus(HttpStatus.OK)
    public void handleStatus(HttpServletRequest request) {
        String status = request.getParameter(STATUS);
        String statusDetail = request.getParameter("status_details");
        String kookooSid = request.getParameter("sid");

        raiseCallEvent(status + "&" + statusDetail, kookooSid);
    }

    private void raiseCallEvent(String kookooEvent, String kookooSid) {
        IvrEvent callEvent = callEvents.get(kookooEvent);
        if (callEvent != null) {
            callFlowServer.raiseCallEvent(callEvent, kookooSid);
        }
    }

    private void constructCallEvents() {
        callEvents = new HashMap<>();
        callEvents.put("NewCall", IvrEvent.Initiated);
        callEvents.put("Hangup", IvrEvent.Hangup);
        callEvents.put("Disconnect", IvrEvent.Disconnected);
        callEvents.put("answered&Normal", IvrEvent.Answered);
        callEvents.put("ring&NoAnswer", IvrEvent.Unanswered);
        callEvents.put("Dial&answered", IvrEvent.DialAnswered);
        callEvents.put("Dial&not_answered", IvrEvent.DialUnanswered);
        callEvents.put("Record", IvrEvent.DialRecord);
    }

    private FlowSession setCustomParams(FlowSession session, HttpServletRequest request) {
        Map<String, Object> params = request.getParameterMap();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (!asList("sid", "cid", "called_number", "event", "data", "duration", STATUS, "tree", "ln").contains(entry.getKey())) {
                session.set(entry.getKey(), (Serializable) entry.getValue());
            }
        }
        return session;
    }

    private FlowSession updateOutgoingCallSessionIdWithKookooSid(String callId, String kookooSid) {
        FlowSession flowSession = flowSessionService.getSession(callId);
        return flowSessionService.updateSessionId(flowSession.getSessionId(), kookooSid);
    }

    private String getDialStatus(HttpServletRequest request) {
        String status = request.getParameter(STATUS);
        if ("answered".equals(status)) {
            return DialStatus.completed.toString();
        } else if ("not_answered".equals(status)) {
            return DialStatus.noAnswer.toString();
        }
        return status;
    }
}
