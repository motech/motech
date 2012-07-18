package org.motechproject.ivr.kookoo.controller;

import org.apache.log4j.Logger;
import org.motechproject.decisiontree.domain.FlowSessionRecord;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooCallbackRequest;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.extensions.CallFlowController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.decisiontree.service.FlowSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IVRController {
    private Logger logger = Logger.getLogger(this.getClass());
    private CallFlowController callFlowController;
    private KookooCallDetailRecordsService kookooCallDetailRecordsService;
    private FlowSessionService flowSessionService;

    @Autowired
    public IVRController(CallFlowController callFlowController, KookooCallDetailRecordsService kookooCallDetailRecordsService, FlowSessionService flowSessionService) {
        this.callFlowController = callFlowController;
        this.kookooCallDetailRecordsService = kookooCallDetailRecordsService;
        this.flowSessionService = flowSessionService;
    }

    @RequestMapping(value = "/ivr/reply", method = RequestMethod.GET)
    public String reply(KookooRequest kookooRequest, HttpServletRequest request, HttpServletResponse response) {
        FlowSessionRecord flowSessionRecord = (FlowSessionRecord) flowSessionService.getSession(kookooRequest.getSid());
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(kookooRequest, request, response, flowSessionRecord);
        return reply(kooKooIVRContext);
    }

    @RequestMapping(value = "/ivr/reply/callback", method = RequestMethod.POST)
    @ResponseBody
    public String callback(KookooCallbackRequest kookooCallbackRequest) {
        if (kookooCallbackRequest.notAnswered()) {
            String callDetailRecordId = kookooCallbackRequest.getCallDetailRecordId();
            kookooCallDetailRecordsService.setCallRecordAsNotAnswered(callDetailRecordId);
            final CallEvent callEvent = new CallEvent(IVREvent.Missed.toString());
            callEvent.appendData(IVRService.CALL_TYPE, kookooCallbackRequest.getCallType());
            kookooCallDetailRecordsService.close(callDetailRecordId, kookooCallbackRequest.getExternalId(), callEvent);
        }
        return "";
    }

    String reply(KooKooIVRContext ivrContext) {
        try {
            ivrContext.setDefaults();
            IVREvent ivrEvent = Enum.valueOf(IVREvent.class, ivrContext.ivrEvent());
            switch (ivrEvent) {
                case NewCall:
                    ivrContext.initialize();
                    if (ivrContext.callDetailRecordId() == null) {
                        String kooKooCallDetailRecordId = kookooCallDetailRecordsService.createAnsweredRecord(ivrContext.callId(), ivrContext.callerId(), ivrContext.callDirection());
                        ivrContext.callDetailRecordId(kooKooCallDetailRecordId);
                    } else {
                        kookooCallDetailRecordsService.setCallRecordAsAnswered(ivrContext.callId(), ivrContext.callDetailRecordId());
                    }
                    kookooCallDetailRecordsService.appendEvent(ivrContext.callDetailRecordId(), ivrEvent, "");
                    break;
                case Disconnect:
                case Hangup:
                    if (flowSessionService.isValidSession(ivrContext.callId())) {
                        kookooCallDetailRecordsService.close(ivrContext.callDetailRecordId(), ivrContext.externalId(), new CallEvent(ivrEvent.toString()));
                        if (ivrContext.isAnswered()) { break; }
                        flowSessionService.removeCallSession(ivrContext.callId());
                    }
                    String url = AllIVRURLs.springTransferUrlToEmptyResponse();
                    logger.info(String.format("Transferring to %s", url));
                    return url;
                case GotDTMF:
                    kookooCallDetailRecordsService.appendEvent(ivrContext.callDetailRecordId(), ivrEvent, ivrContext.userInput());
            }
            String url = callFlowController.urlFor(ivrContext);
            if (AllIVRURLs.DECISION_TREE_URL.equals(url)) {
                String treeName = callFlowController.decisionTreeName(ivrContext);
                ivrContext.treeName(treeName);
            }
            flowSessionService.updateSession(ivrContext.getFlowSessionRecord());
            kookooCallDetailRecordsService.appendToLastCallEvent(ivrContext.callDetailRecordId(), ivrContext.dataToLog());
            String transferURL = AllIVRURLs.springTransferUrl(url, ivrContext.ivrEvent().toLowerCase());
            logger.info(String.format("Transferring to %s", transferURL));
            return transferURL;
        } catch (Exception e) {
            logger.error("Failed to handled incoming request", e);
            String url = AllIVRURLs.springTransferUrlToUnhandledError();
            logger.info(String.format("Transferring to %s", url));
            return url;
        }
    }
}
