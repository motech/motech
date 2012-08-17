package org.motechproject.server.verboice.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.CallStatus;
import org.motechproject.decisiontree.service.FlowSessionService;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/verboice")
public class VerboiceIVRController {

    public static final String DECISIONTREE_URL = "/decisiontree/node";

    @Autowired
    private VerboiceIVRService verboiceIVRService;
    @Autowired
    private FlowSessionService flowSessionService;

    public VerboiceIVRController() {
    }

    @RequestMapping("/ivr")
    public String handleRequest(HttpServletRequest request) {
        final String treeName = request.getParameter("tree");
        String callId = request.getParameter("motech_call_id");
        String verboiceCallId = request.getParameter("CallSid");
        updateOutgoingCallSessionIdWithVerboiceSid(callId, verboiceCallId);
        if (StringUtils.isNotBlank(treeName)) {
            String digits = request.getParameter("DialCallStatus");
            if (StringUtils.isBlank(digits)) {
                digits = request.getParameter("Digits");
            }
            String treePath = request.getParameter("trP");
            String language = request.getParameter("ln");
            return redirectToDecisionTree(treeName, digits, treePath, language, verboiceCallId, request.getServletPath());
        }

        return verboiceIVRService.getHandler().handle(request.getParameterMap());
    }

    @RequestMapping("/ivr/hangup")
    public String handleHangup(HttpServletRequest request) {
        final String treeName = request.getParameter("tree");
        String callId = request.getParameter("motech_call_id");
        String verboiceCallId = request.getParameter("CallSid");
        updateOutgoingCallSessionIdWithVerboiceSid(callId, verboiceCallId);
        String treePath = request.getParameter("trP");
        String language = request.getParameter("ln");
        return redirectToDecisionTree(treeName, CallStatus.hangup.toString(), treePath, language, verboiceCallId, request.getServletPath());
    }

    private void updateOutgoingCallSessionIdWithVerboiceSid(String callId, String verboiceCallId) {
        if (callId != null) {
            FlowSession flowSession = flowSessionService.getSession(callId);
            flowSessionService.updateSessionId(flowSession.getSessionId(), verboiceCallId);
        }
    }

    private String redirectToDecisionTree(String treeName, String digits, String treePath, String language, String flowSessionId, String servletPath) {
        final String transitionKey = digits == null ? "" : "&trK=" + digits;
        final String flowSessionParam = flowSessionId == null? "" :"&"+ FlowSessionService.FLOW_SESSION_ID_PARAM +"="+ flowSessionId;
        return String.format("forward:%s%s?provider=verboice&tree=%s&trP=%s&ln=%s%s%s", servletPath, DECISIONTREE_URL, treeName, treePath, language, flowSessionParam, transitionKey)
                .replaceAll("//", "/");
    }
}
