package org.motechproject.ivr.kookoo.controller;

import org.apache.log4j.Logger;
import org.motechproject.ivr.kookoo.*;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.IVRMessage;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class SafeIVRController {
    static final String NEW_CALL_URL_ACTION = "newcall";
    static final String GOT_DTMF_URL_ACTION = "gotdtmf";

    protected Logger logger = Logger.getLogger(this.getClass());
    protected IVRMessage ivrMessage;
    private KookooCallDetailRecordsService callDetailRecordsService;

    protected SafeIVRController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService) {
        this.ivrMessage = ivrMessage;
        if (callDetailRecordsService == null) throw new NullPointerException(String.format("%s cannot be null", KookooCallDetailRecordsService.class.getName()));
        this.callDetailRecordsService = callDetailRecordsService;
    }

    @RequestMapping(value = NEW_CALL_URL_ACTION, method = RequestMethod.GET)
    @ResponseBody
    public final String safeNewCall(@ModelAttribute KookooRequest kooKooRequest, HttpServletRequest request, HttpServletResponse response) {
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(kooKooRequest, request, response);
        return safeCall(kooKooIVRContext);
    }

    String safeCall(KooKooIVRContext ivrContext) {
        try {
            //next line is slightly bad but better than duplicate code, need function pointers
            KookooIVRResponseBuilder kookooIVRResponseBuilder = NEW_CALL_URL_ACTION.equalsIgnoreCase(ivrContext.ivrEvent()) ? newCall(ivrContext) : gotDTMF(ivrContext);
            if (kookooIVRResponseBuilder.isHangUp()) closeCallRecord(ivrContext);
            String responseXML = kookooIVRResponseBuilder.create(ivrMessage);
            logger.info(String.format(" XML returned: %s", responseXML));
            return responseXML;
        } catch (Exception e) {
            closeCallRecord(ivrContext);
            logger.error(String.format("Failed to process incoming %s request", ivrContext.ivrEvent()), e);
            String url = AllIVRURLs.springTranferUrlToUnhandledError();
            logger.info(String.format("Transferring to %s", url));
            return url;
        }
    }

    private void closeCallRecord(KooKooIVRContext kooKooIVRContext) {
        callDetailRecordsService.close(kooKooIVRContext.callDetailRecordId(), kooKooIVRContext.externalId(), "Hangup");
        kooKooIVRContext.invalidateSession();
    }

    @RequestMapping(value = GOT_DTMF_URL_ACTION, method = RequestMethod.GET)
    @ResponseBody
    public final String safeGotDTMF(@ModelAttribute KookooRequest kooKooRequest, HttpServletRequest request, HttpServletResponse response) {
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(kooKooRequest, request, response);
        return safeCall(kooKooIVRContext);
    }

    @RequestMapping(value = "disconnect", method = RequestMethod.GET)
    @ResponseBody
    public final String disconnect(HttpServletRequest request) {
        return "";
    }

    @RequestMapping(value = "hangup", method = RequestMethod.GET)
    @ResponseBody
    public final String hangup(HttpServletRequest request) {
        return "";
    }

    public KookooIVRResponseBuilder newCall(KooKooIVRContext kooKooIVRContext) {
        throw new UnsupportedOperationException("The extending controller should have implemeted this kookoo event.");
    }

    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        throw new UnsupportedOperationException("The extending controller should have implemeted this kookoo event.");
    }
}
