package org.motechproject.server.verboice.web;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.callflow.domain.CallDetailRecord;
import org.motechproject.callflow.domain.CallDirection;
import org.motechproject.callflow.domain.FlowSessionRecord;
import org.motechproject.callflow.service.CallFlowServer;
import org.motechproject.callflow.service.FlowSessionService;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.CallStatus;
import org.motechproject.ivr.service.SessionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;

@Controller
public class VerboiceIVRController {

    private static final String VERBOICE_CALL_SID = "CallSid";
    private static final String MOTECH_CALL_ID = "motech_call_id";
    private static final String VERBOICE_FROM_PHONE_PARAM = "From";
    private Logger logger = Logger.getLogger(VerboiceIVRController.class);

    @Autowired
    private FlowSessionService flowSessionService;
    @Autowired
    private CallFlowServer callFlowServer;

    public VerboiceIVRController() {
    }

    @RequestMapping("/ivr")
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response) {
        String verboiceCallId = request.getParameter(VERBOICE_CALL_SID);
        String motechCallId = request.getParameter(MOTECH_CALL_ID);
        String phoneNumber = request.getParameter(VERBOICE_FROM_PHONE_PARAM);
        FlowSession session = null;
        if (motechCallId == null) {
            session = flowSessionService.findOrCreate(verboiceCallId, phoneNumber);
            final CallDetailRecord callDetailRecord = ((FlowSessionRecord) session).getCallDetailRecord();
            callDetailRecord.setCallDirection(CallDirection.Inbound);
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

        ModelAndView view = callFlowServer.getResponse(verboiceCallId, phoneNumber, "verboice", tree, digits, language);
        view.addObject("contextPath", request.getContextPath());
        view.addObject("servletPath", request.getServletPath());
        view.addObject("host", request.getHeader("Host"));
        view.addObject("scheme", request.getScheme());
        return view;
    }

    @RequestMapping(value = "/ivr/callstatus", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void handleMissedCall(HttpServletRequest request) {
        String callStatus = request.getParameter("CallStatus");
        logger.info("Verboice status callback : " + callStatus);

        if ("completed".equals(callStatus)) {
            String language = request.getParameter("ln");
            String verboiceCallId = request.getParameter(VERBOICE_CALL_SID);
            String phoneNumber = request.getParameter(VERBOICE_FROM_PHONE_PARAM);
            String tree = request.getParameter("tree");
            callFlowServer.getResponse(verboiceCallId, phoneNumber, "verboice",tree, CallStatus.Disconnect.toString(), language);
        }

        List<String> missedCallStatuses = Arrays.asList("busy", "failed", "no-answer");
        if (callStatus == null || callStatus.trim().isEmpty() || !missedCallStatuses.contains(callStatus)) {
            return;
        }
        String motechCallId = request.getParameter(MOTECH_CALL_ID);
        FlowSession session = flowSessionService.getSession(motechCallId);
        if (session == null) {
            throw new SessionNotFoundException("No session found! [Session Id " + motechCallId + "]");
        }
        String callSid = request.getParameter(VERBOICE_CALL_SID);
        session = flowSessionService.updateSessionId(motechCallId, callSid);
        callFlowServer.handleMissedCall(session.getSessionId());
    }

    private FlowSession updateOutgoingCallSessionIdWithVerboiceSid(String callId, String verboiceCallId) {
        FlowSession flowSession = flowSessionService.getSession(callId);
        return flowSessionService.updateSessionId(flowSession.getSessionId(), verboiceCallId);
    }

    private FlowSession setCustomParams(FlowSession session, HttpServletRequest request) {
        Map params = request.getParameterMap();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            if (!asList(VERBOICE_CALL_SID, "AccountSid", VERBOICE_FROM_PHONE_PARAM, "To", "CallStatus", "ApiVersion", "Direction", "ForwardedFrom", "CallerName", "FromCity", "FromState", "FromZip", "FromCountry", "ToCity", "ToState", "ToZip", "ToCountry", "ln").contains(key)) {
                session.set(key, (Serializable) params.get(key));
            }
        }
        return session;
    }
}
