package org.motechproject.ivr.kookoo.controller;

import org.apache.log4j.Logger;
import org.motechproject.ivr.domain.CallSessionRecord;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.domain.IVRMessage;
import org.motechproject.ivr.service.IVRSessionManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public abstract class SafeIVRController {
    static final String NEW_CALL_URL_ACTION = "/newcall";
    static final String GOT_DTMF_URL_ACTION = "/gotdtmf";
    static final String DIAL_URL_ACTION = "/dial";
    static final String HANGUP_URL_ACTION = "/hangup";
    static final String DISCONNECT_URL_ACTION = "/disconnect";

    protected Logger logger = Logger.getLogger(this.getClass());
    protected IVRMessage ivrMessage;
    private StandardResponseController standardResponseController;
    private KookooCallDetailRecordsService callDetailRecordsService;

    @Autowired
    private IVRSessionManagementService ivrSessionManagementService;

    protected SafeIVRController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, StandardResponseController standardResponseController) {
        this.ivrMessage = ivrMessage;
        this.standardResponseController = standardResponseController;
        if (callDetailRecordsService == null) {
            throw new NullPointerException(String.format("%s cannot be null", KookooCallDetailRecordsService.class.getName()));
        }
        this.callDetailRecordsService = callDetailRecordsService;
    }

    protected SafeIVRController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService,
                                StandardResponseController standardResponseController,
                                IVRSessionManagementService ivrSessionManagementService) {
        this(ivrMessage, callDetailRecordsService, standardResponseController);
        this.ivrSessionManagementService = ivrSessionManagementService;
    }

    @RequestMapping(value = NEW_CALL_URL_ACTION, method = RequestMethod.GET)
    @ResponseBody
    public final String safeNewCall(@ModelAttribute KookooRequest kooKooRequest, HttpServletRequest request, HttpServletResponse response) {
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(kooKooRequest, request, response,
                getCallSessionRecord(kooKooRequest.getSid()));
        return safeCall(kooKooIVRContext);
    }

    @RequestMapping(value = GOT_DTMF_URL_ACTION, method = RequestMethod.GET)
    @ResponseBody
    public final String safeGotDTMF(@ModelAttribute KookooRequest kooKooRequest, HttpServletRequest request, HttpServletResponse response) {
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(kooKooRequest, request, response, getCallSessionRecord(kooKooRequest.getSid()));
        return safeCall(kooKooIVRContext);
    }

    @RequestMapping(value = DIAL_URL_ACTION, method = RequestMethod.GET)
    @ResponseBody
    public final String safeDial(@ModelAttribute KookooRequest kooKooRequest, HttpServletRequest request, HttpServletResponse response) {
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(kooKooRequest, request, response, getCallSessionRecord(kooKooRequest.getSid()));
        return safeCall(kooKooIVRContext);
    }

    @RequestMapping(value = HANGUP_URL_ACTION, method = RequestMethod.GET)
    @ResponseBody
    public final String hangup(@ModelAttribute KookooRequest kooKooRequest, HttpServletRequest request, HttpServletResponse response) {
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(kooKooRequest, request, response, getCallSessionRecord(kooKooRequest.getSid()));
        return hangup(kooKooIVRContext);
    }

    @RequestMapping(value = DISCONNECT_URL_ACTION, method = RequestMethod.GET)
    @ResponseBody
    public final String disconnect(HttpServletRequest request) {
        return "";
    }

    private CallSessionRecord getCallSessionRecord(String sessionId) {
        return ivrSessionManagementService.getCallSession(sessionId);
    }

    private String safeCall(KooKooIVRContext ivrContext) {
        try {
            IVREvent ivrEvent = Enum.valueOf(IVREvent.class, ivrContext.ivrEvent());
            KookooIVRResponseBuilder kookooIVRResponseBuilder;
            switch (ivrEvent) {
                case NewCall:
                    kookooIVRResponseBuilder = newCall(ivrContext);
                    break;
                case Dial:
                    kookooIVRResponseBuilder = dial(ivrContext);
                    break;
                default:
                    kookooIVRResponseBuilder = gotDTMF(ivrContext);
            }
            String responseXML = kookooIVRResponseBuilder.create(ivrMessage);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(CallEventConstants.CUSTOM_DATA_LIST, responseXML);
            callDetailRecordsService.appendToLastCallEvent(ivrContext.callDetailRecordId(), map);
            if (kookooIVRResponseBuilder.isHangUp()) standardResponseController.prepareForHangup(ivrContext);
            logger.info(String.format(" XML returned: %s", responseXML));
            return responseXML;
        } catch (Exception e) {
            logger.error(String.format("Failed to process incoming %s request", ivrContext.ivrEvent()), e);
            return standardResponseController.hangup(ivrContext);
        }
    }

    public KookooIVRResponseBuilder newCall(KooKooIVRContext kooKooIVRContext) {
        throw new UnsupportedOperationException("The extending controller should have implemeted this kookoo event.");
    }

    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        throw new UnsupportedOperationException("The extending controller should have implemeted this kookoo event.");
    }

    public KookooIVRResponseBuilder dial(KooKooIVRContext kooKooIVRContext) {
        throw new UnsupportedOperationException("The extending controller should have implemeted this kookoo event.");
    }

    public String hangup(KooKooIVRContext kooKooIVRContext) {
        ivrSessionManagementService.removeCallSession(kooKooIVRContext.callId());
        return KookooResponseFactory.empty(kooKooIVRContext.callId()).create(null);
    }
}
