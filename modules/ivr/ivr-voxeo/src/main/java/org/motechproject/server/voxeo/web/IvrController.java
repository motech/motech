package org.motechproject.server.voxeo.web;

import org.ektorp.UpdateConflictException;
import org.motechproject.ivr.event.IVREventDelegate;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.scheduler.context.EventContext;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.EventRelay;
import org.motechproject.server.voxeo.VoxeoIVRService;
import org.motechproject.server.voxeo.dao.AllPhoneCalls;
import org.motechproject.server.voxeo.domain.PhoneCall;
import org.motechproject.server.voxeo.domain.PhoneCallEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * IVR Event handler for Voxeo handles flash and incoming call events and records it in  {@link org.motechproject.server.voxeo.domain.PhoneCall PhoneCall}
 */
@Controller
public class IvrController extends MultiActionController {
    private static final int DEFAULT_FLASH_SLEEP = 5000;
    private EventRelay eventRelay = EventContext.getInstance().getEventRelay();

    private AllPhoneCalls allPhoneCalls;

    @Autowired
    @Qualifier("VoxeoIVRService")
    private IVRService voxeoIVRService;

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    /**
     * Flash is disconnecting incoming call and calling back same number. This is useful in saving call cost where incoming calls are free.
     * Reads incoming caller id and calls back after 5 seconds
     * @param request
     * @param response
     */
    @RequestMapping("/flash")
    public void flash(HttpServletRequest request, HttpServletResponse response) {
        String phoneNumber = request.getParameter("phoneNumber");
        String applicationName = request.getParameter("applicationName");

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(VoxeoIVRService.APPLICATION_NAME, applicationName);

        sleep(DEFAULT_FLASH_SLEEP);    // TODO: configurable param
        voxeoIVRService.initiateCall(new CallRequest(phoneNumber, params, ""));
    }

    /**
     * Handles incoming calls and records phone call details in data-store.
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/incoming")
    public ModelAndView incoming(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        ModelAndView mav = new ModelAndView();

        String sessionId = request.getParameter("session.id");
        String status = request.getParameter("status");
        String callerId = request.getParameter("application.callerId");
        String timestamp = request.getParameter("timestamp");

        logger.info("Recording event for inbound call from " + callerId + " [sessionId: " + sessionId + ", status: " + status + ", timestamp: " + timestamp + "]");

        // See if I can load a CallDetailRecord for this session
        PhoneCall phoneCall = allPhoneCalls.findBySessionId(sessionId);

        if (null == phoneCall) {
            phoneCall = new PhoneCall();
            phoneCall.setId(sessionId);
            phoneCall.setSessionId(sessionId);
            phoneCall.setDirection(PhoneCall.Direction.INCOMING);

            CallRequest callRequest = new CallRequest();
            phoneCall.setCallRequest(callRequest);

            try {
                allPhoneCalls.add(phoneCall);
            } catch (UpdateConflictException e) {
                // I eat this exception since it means there was a race condition and the document has already been created.
                // I can continue with the new version
                phoneCall = allPhoneCalls.findBySessionId(sessionId);
            }
        }

        PhoneCallEvent event = new PhoneCallEvent();
        event.setCallerId(callerId);
        event.setStatus(PhoneCallEvent.Status.valueOf(status));
        event.setTimestamp(new Long(timestamp));

        phoneCall.addEvent(event);

        updateState(phoneCall, event);

        // TODO: I should retry a couple of times with exponential backoff. Or move events to a seperate document
        try {
            allPhoneCalls.update(phoneCall);
        } catch (UpdateConflictException e) {
            // I eat this exception since it means there was a race condition and the document has already been created.
            // I can continue with the new version
            phoneCall = allPhoneCalls.findBySessionId(sessionId);
            phoneCall.addEvent(event);
            allPhoneCalls.update(phoneCall);
        }

        return mav;
    }

    @RequestMapping(value = "/outgoing")
    public ModelAndView outgoing(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        ModelAndView mav = new ModelAndView();


        String sessionId = request.getParameter("session.id");
        String externalId = request.getParameter("externalId");
        String status = request.getParameter("status");
        String reason = request.getParameter("reason");
        String callerId = "1234567890";
        //String callerId = request.getParameter("application.callerId").substring(4);
        String timestamp = request.getParameter("timestamp");

        logger.info("Recording event for outgoing call to " + callerId + " [externalId: " + externalId + "sessionId: " + sessionId + ", status: " + status + ", timestamp: " + timestamp + "]");

        // See if I can load a CallDetailRecord for this session
        PhoneCall phoneCall = allPhoneCalls.get(externalId);

        if (null == phoneCall) {
            // This shouldn't happen
            phoneCall = new PhoneCall();
            phoneCall.setStartDate(new Date());
            phoneCall.setSessionId(sessionId);
            phoneCall.setDirection(PhoneCall.Direction.OUTGOING);
            phoneCall.setId(externalId);

            logger.error("Outgoing call without a phone call record. (externalId: " + externalId);
        }

        PhoneCallEvent event = new PhoneCallEvent();
        event.setCallerId(callerId);
        event.setStatus(PhoneCallEvent.Status.valueOf(status));
        event.setTimestamp(new Long(timestamp));
        event.setReason(PhoneCallEvent.Reason.fromString(reason));

        phoneCall.addEvent(event);

        updateState(phoneCall, event);

        // TODO: I should retry a couple of times with exponential backoff. Or move events to a seperate document
        try {
            allPhoneCalls.update(phoneCall);
        } catch (UpdateConflictException e) {
            // I eat this exception since it means there was a race condition and the document has already been created.
            // I can continue with the new version
            phoneCall = allPhoneCalls.findBySessionId(externalId);
            phoneCall.addEvent(event);
            allPhoneCalls.update(phoneCall);
        }

        return null;
    }

    private void updateState(PhoneCall phoneCall, PhoneCallEvent event) {
        MotechEvent motechEvent = null;

        logger.info("Updating event status: " + event.getStatus());
        switch (event.getStatus()) {
            case ALERTING:
                phoneCall.setStartDate(new Date(event.getTimestamp()));
                break;

            case CONNECTED:
                phoneCall.setAnswerDate(new Date(event.getTimestamp()));
                break;

            case DIALOG_EXIT:
            case DISCONNECTED:
                phoneCall.setDisposition(CallDetailRecord.Disposition.ANSWERED);

                motechEvent = phoneCall.getCallRequest().getOnSuccessEvent();
                phoneCall.setEndDate(new Date(event.getTimestamp()));
                break;

            case FAILED:
                phoneCall.setEndDate(new Date(event.getTimestamp()));
                switch (event.getReason()) {
                    case BUSY:
                        phoneCall.setDisposition(CallDetailRecord.Disposition.BUSY);
                        motechEvent = phoneCall.getCallRequest().getOnBusyEvent();
                        break;

                    case TIMEOUT:
                        phoneCall.setDisposition(CallDetailRecord.Disposition.NO_ANSWER);
                        motechEvent = phoneCall.getCallRequest().getOnNoAnswerEvent();
                        break;

                    default:
                        phoneCall.setDisposition(CallDetailRecord.Disposition.FAILED);
                        motechEvent = phoneCall.getCallRequest().getOnFailureEvent();
                }
                break;

            default:
                logger.error("Unknown event: " + event.getStatus());
                break;
        }

        if (null != motechEvent) {
            CallDetailRecord cdr = new CallDetailRecord(phoneCall.getStartDate(), phoneCall.getEndDate(), phoneCall.getAnswerDate(),
                    phoneCall.getDisposition(), phoneCall.getDuration());

            Map<String, Object> parameters = motechEvent.getParameters();
            parameters.put(IVREventDelegate.CALL_DETAIL_RECORD_KEY, cdr);

            eventRelay.sendEventMessage(motechEvent);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
