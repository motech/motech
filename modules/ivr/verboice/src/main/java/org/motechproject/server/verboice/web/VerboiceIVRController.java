package org.motechproject.server.verboice.web;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.ivr.domain.CallEventLog;
import org.motechproject.callflow.domain.FlowSessionRecord;
import org.motechproject.callflow.domain.IvrEvent;
import org.motechproject.callflow.service.CallFlowServer;
import org.motechproject.callflow.service.FlowSessionService;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.ivr.domain.CallDisposition;
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
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

@Controller
public class VerboiceIVRController {

    private static final String VERBOICE_CALL_SID = "CallSid";
    private static final String VERBOICE_FROM_PHONE_PARAM = "From";
    private Logger logger = Logger.getLogger(VerboiceIVRController.class);
    private Map<String, IvrEvent> callEvents;

    @Autowired
    private FlowSessionService flowSessionService;
    @Autowired
    private CallFlowServer callFlowServer;

    public VerboiceIVRController() {
        constructCallEvents();
    }

    @RequestMapping("/ivr")
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response) {
        String verboiceCallId = request.getParameter(VERBOICE_CALL_SID);
        String phoneNumber = request.getParameter(VERBOICE_FROM_PHONE_PARAM);
        FlowSession session = null;
        session = flowSessionService.getSession(verboiceCallId);
        if (session == null) {
            session = flowSessionService.findOrCreate(verboiceCallId, phoneNumber);
            final CallDetailRecord callDetailRecord = ((FlowSessionRecord) session).getCallDetailRecord();
            callDetailRecord.setCallDirection(CallDirection.Inbound);
        }

        String tree = request.getParameter("tree");
        String language = request.getParameter("ln");
        String digits = request.getParameter("DialCallStatus");

        raiseCallEvent("dial&" + digits, verboiceCallId);

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
    public void handleStatus(HttpServletRequest request) {
        String callStatus = request.getParameter("CallStatus");
        String callSid = request.getParameter(VERBOICE_CALL_SID);
        String phoneNum = request.getParameter(VERBOICE_FROM_PHONE_PARAM);

        logger.info("Verboice status callback : " + callStatus);

        updateRecord(callStatus, callSid, phoneNum);

        raiseCallEvent(callStatus, callSid);
    }

    private void raiseCallEvent(String callStatus, String callSid) {
        IvrEvent ivrEvent = callEvents.get(callStatus);
        if (ivrEvent != null) {
            callFlowServer.raiseCallEvent(ivrEvent, callSid);
        }
    }

    private void constructCallEvents() {
        callEvents = new HashMap<>();
        callEvents.put("queued", IvrEvent.Queued);
        callEvents.put("ringing", IvrEvent.Ringing);
        callEvents.put("in-progress", IvrEvent.Initiated);
        callEvents.put("no-answer", IvrEvent.Missed);
        callEvents.put("busy", IvrEvent.Busy);
        callEvents.put("failed", IvrEvent.Failed);
        callEvents.put("completed", IvrEvent.Answered);
        callEvents.put("dial&in-progress", IvrEvent.DialInitiated);
        callEvents.put("dial&no-answer", IvrEvent.DialMissed);
        callEvents.put("dial&busy", IvrEvent.DialBusy);
        callEvents.put("dial&failed", IvrEvent.DialFailed);
        callEvents.put("dial&completed", IvrEvent.DialAnswered);
    }

    private void updateRecord(String callStatus, String callSid, String phoneNumber) {
        FlowSessionRecord record = (FlowSessionRecord) flowSessionService.getSession(callSid);
        if (record != null) {

            CallDetailRecord callDetail = record.getCallDetailRecord();
            CallEventLog callEventLog = new CallEventLog(callStatus);
            callDetail.addCallEvent(callEventLog);

            if ("ringing".equals(callStatus)) {
                callDetail.setDisposition(CallDisposition.UNKNOWN);
            } else if ("in-progress".equals(callStatus)) {
                callDetail.setDisposition(CallDisposition.ANSWERED);
            } else if ("completed".equals(callStatus)) {
                callDetail.setDisposition(CallDisposition.ANSWERED);
            } else if ("failed".equals(callStatus)) {
                callDetail.setDisposition(CallDisposition.FAILED);
            } else if ("busy".equals(callStatus)) {
                callDetail.setDisposition(CallDisposition.BUSY);
            } else if ("no-answer".equals(callStatus)) {
                callDetail.setDisposition(CallDisposition.NO_ANSWER);
            }

            flowSessionService.updateSession(record);
        } else {
            record = (FlowSessionRecord) flowSessionService.findOrCreate(callSid, phoneNumber);
            final CallDetailRecord callDetailRecord = record.getCallDetailRecord();
            callDetailRecord.setCallDirection(CallDirection.Inbound);
            flowSessionService.updateSession(record);
        }
    }

    private FlowSession setCustomParams(FlowSession session, HttpServletRequest request) {

        Map<String, Object> params = request.getParameterMap();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (!asList(VERBOICE_CALL_SID, "AccountSid", VERBOICE_FROM_PHONE_PARAM, "To", "CallStatus", "ApiVersion", "Direction", "ForwardedFrom", "CallerName", "FromCity", "FromState", "FromZip", "FromCountry", "ToCity", "ToState", "ToZip", "ToCountry", "ln").contains(entry.getKey())) {
                session.set(entry.getKey(), (Serializable) entry.getValue());
            }
        }
        return session;
    }
}
