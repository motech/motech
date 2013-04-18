package org.motechproject.server.kookoo.web;

import org.motechproject.callflow.service.CallFlowServer;
import org.motechproject.callflow.service.FlowSessionService;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.CallStatus;
import org.motechproject.decisiontree.core.model.DialStatus;
import org.motechproject.ivr.service.SessionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Map;
import static java.util.Arrays.asList;

@Controller
@RequestMapping("/kookoo")
public class KookooIvrController {

    private CallFlowServer decisionTreeServer;
    private FlowSessionService flowSessionService;

    @Autowired
    public KookooIvrController(CallFlowServer decisionTreeServer, FlowSessionService flowSessionService) {
        this.decisionTreeServer = decisionTreeServer;
        this.flowSessionService = flowSessionService;
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
        if ("GotDTMF".equals(event)) {
            transitionKey = request.getParameter("data");
        } else if ("Hangup".equals(event)) {
            transitionKey = CallStatus.Hangup.toString();
        } else if ("Disconnect".equals(event)) {
            transitionKey = CallStatus.Disconnect.toString();
        } else if ("Dial".equals(event)) {
            transitionKey = getDialStatus(request);
        }

        String tree = request.getParameter("tree");
        String language = request.getParameter("ln");

        session.setLanguage(language);
        session = setCustomParams(session, request);
        flowSessionService.updateSession(session);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        ModelAndView view = decisionTreeServer.getResponse(kookooSid, phoneNumber, "kookoo", tree, transitionKey, language);
        view.addObject("contextPath", request.getContextPath());
        view.addObject("servletPath", request.getServletPath());
        view.addObject("host", request.getHeader("Host"));
        view.addObject("scheme", request.getScheme());
        return view;
    }

    @RequestMapping("/status")
    @ResponseBody
    public String statusCallback(HttpServletRequest request, HttpServletResponse response) {
        return "";
    }

    @RequestMapping(value = "/ivr/callstatus", method = RequestMethod.POST)
    public void handleMissedCall(HttpServletRequest request) {
        String status = request.getParameter("status_details");
        if (status == null || status.trim().isEmpty() || !status.equals("NoAnswer")) {
            return;
        }
        String motechCallId = request.getParameter("motech_call_id");
        FlowSession session = flowSessionService.getSession(motechCallId);
        if (session == null) {
            throw new SessionNotFoundException("No session found! [Session Id " + motechCallId + "]");
        }
        String kookooSid = request.getParameter("sid");
        session = flowSessionService.updateSessionId(motechCallId, kookooSid);
        decisionTreeServer.handleMissedCall(session.getSessionId());
    }


    private FlowSession setCustomParams(FlowSession session, HttpServletRequest request) {
        Map<String,Object> params = request.getParameterMap();
        for (Map.Entry<String,Object> entry : params.entrySet()) {
            if (!asList("sid", "cid", "called_number", "event", "data", "duration", "status", "tree", "ln").contains(entry.getKey())) {
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
        String status = request.getParameter("status");
        if ("answered".equals(status)) {
            return DialStatus.completed.toString();
        } else if ("not_answered".equals(status)) {
            return DialStatus.noAnswer.toString();
        }
        return status;
    }
}
