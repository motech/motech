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
        if (verboiceCallId != null) {
            updateOutgoingCallSessionIdWithVerboiceSid(request.getParameter("motech_call_id"), verboiceCallId);
        }

        String tree = request.getParameter("tree");
        String phoneNumber = request.getParameter("From");
        String language = request.getParameter("ln");
        String digits = request.getParameter("DialCallStatus");
        if (StringUtils.isBlank(digits)) {
            digits = request.getParameter("Digits");
        }

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        ModelAndView view = decisionTreeServer.getResponse(verboiceCallId, phoneNumber, "verboice", tree, digits, language);
        view.addObject("contextPath", request.getContextPath());
        view.addObject("servletPath", request.getServletPath());
        view.addObject("host", request.getHeader("Host"));
        view.addObject("scheme", request.getScheme());
        return view;
    }

    private void updateOutgoingCallSessionIdWithVerboiceSid(String callId, String verboiceCallId) {
        if (callId != null) {
            FlowSession flowSession = flowSessionService.getSession(callId);
            flowSessionService.updateSessionId(flowSession.getSessionId(), verboiceCallId);
        }
    }
}
