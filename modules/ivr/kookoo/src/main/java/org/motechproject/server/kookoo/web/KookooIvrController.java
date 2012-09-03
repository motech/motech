    package org.motechproject.server.kookoo.web;

import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.CallStatus;
import org.motechproject.decisiontree.server.service.DecisionTreeServer;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;

@Controller
@RequestMapping("/kookoo")
public class KookooIvrController {

    private DecisionTreeServer decisionTreeServer;
    private FlowSessionService flowSessionService;

    @Autowired
    public KookooIvrController(DecisionTreeServer decisionTreeServer, FlowSessionService flowSessionService) {
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
        } else if("Hangup".equals(event)) {
            transitionKey = CallStatus.Hangup.toString();
        } else if("Disconnect".equals(event)) {
            transitionKey = CallStatus.Disconnect.toString();
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
    public String statusCallback(HttpServletRequest request, HttpServletResponse response) {
        return "";
    }

    private FlowSession setCustomParams(FlowSession session, HttpServletRequest request) {
        Map params = request.getParameterMap();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            if (!asList("sid", "cid", "called_number", "event", "data", "duration", "status", "tree", "ln").contains(key)) {
                session.set(key, (Serializable) params.get(key));
            }
        }
        return session;
    }

    private FlowSession updateOutgoingCallSessionIdWithKookooSid(String callId, String kookooSid) {
        FlowSession flowSession = flowSessionService.getSession(callId);
        return flowSessionService.updateSessionId(flowSession.getSessionId(), kookooSid);
    }
}
