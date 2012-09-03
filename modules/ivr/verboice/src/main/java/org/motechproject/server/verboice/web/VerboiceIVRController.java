package org.motechproject.server.verboice.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.server.service.DecisionTreeServer;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.server.verboice.VerboiceIVRService;
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
@RequestMapping("/verboice")
public class VerboiceIVRController {

    @Autowired
    private VerboiceIVRService verboiceIVRService;
    @Autowired
    private FlowSessionService flowSessionService;
    @Autowired
    DecisionTreeServer decisionTreeServer;

    public VerboiceIVRController() {
    }

    @RequestMapping("/ivr")
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response) {
        String verboiceCallId = request.getParameter("CallSid");
        String motechCallId = request.getParameter("motech_call_id");
        String phoneNumber = request.getParameter("From");
        FlowSession session = null;
        if (motechCallId == null) {
            session = flowSessionService.findOrCreate(verboiceCallId, phoneNumber);
        } else {
            session = updateOutgoingCallSessionIdWithVerboiceSid(motechCallId, verboiceCallId);
        }

        String tree = request.getParameter("tree");
        String language = request.getParameter("ln");
        String digits = request.getParameter("DialCallStatus");
        if (StringUtils.isBlank(digits)) {
            digits = request.getParameter("Digits");
        }

        session.setLanguage(language);
        session = setCustomParams(session, request);
        flowSessionService.updateSession(session);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        ModelAndView view = decisionTreeServer.getResponse(verboiceCallId, phoneNumber, "verboice", tree, digits, language);
        view.addObject("contextPath", request.getContextPath());
        view.addObject("servletPath", request.getServletPath());
        view.addObject("host", request.getHeader("Host"));
        view.addObject("scheme", request.getScheme());
        return view;
    }

    private FlowSession updateOutgoingCallSessionIdWithVerboiceSid(String callId, String verboiceCallId) {
        FlowSession flowSession = flowSessionService.getSession(callId);
        return flowSessionService.updateSessionId(flowSession.getSessionId(), verboiceCallId);
    }

    private FlowSession setCustomParams(FlowSession session, HttpServletRequest request) {
        Map params = request.getParameterMap();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            if (!asList("CallSid", "AccountSid", "From",  "To", "CallStatus", "ApiVersion", "Direction", "ForwardedFrom", "CallerName", "FromCity", "FromState", "FromZip", "FromCountry", "ToCity", "ToState", "ToZip", "ToCountry", "ln").contains(key)) {
                session.set(key, (Serializable) params.get(key));
            }
        }
        return session;
    }
}
