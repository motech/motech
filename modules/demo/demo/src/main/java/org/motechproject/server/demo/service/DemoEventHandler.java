package org.motechproject.server.demo.service;

import org.motechproject.ivr.model.CallInitiationException;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.demo.EventKeys;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DemoEventHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final int TIMEOUT = 30;

    private IVRService ivrService;

    @MotechListener(subjects = { EventKeys.CALL_EVENT_SUBJECT })
    public void call(MotechEvent event) {
        if (ivrService == null) {
            logger.error("IVR service is not available!");
            return;
        }


        String phoneNumber = EventKeys.getPhoneNumber(event);
        if (null == phoneNumber || 0 == phoneNumber.length()) {
            logger.error("Can not handle Event: " + event.getSubject()
                    + ". The event is invalid - missing the "
                    + EventKeys.PHONE_KEY + " parameter");
            return;
        }

        try {
            CallRequest callRequest = new CallRequest(phoneNumber, TIMEOUT, "vxml.url");

            ivrService.initiateCall(callRequest);
        } catch (CallInitiationException e) {
            logger.error("Unable to initiate call to PhoneNumber:" + phoneNumber, e);
        }
    }

    public IVRService getIvrService() {
        return ivrService;
    }

    public void setIvrService(IVRService ivrService) {
        this.ivrService = ivrService;
    }
}
