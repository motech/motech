package org.motechproject.callflow;

import org.apache.log4j.Logger;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.service.contract.CallRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.decisiontree.core.EventKeys.END_OF_CALL_EVENT;

@Component
public class EndOfCallListener {
    private Logger log = Logger.getLogger(EndOfCallListener.class);


    @Autowired
    private CallRecordsService callRecordsService;

    @MotechListener(subjects = {END_OF_CALL_EVENT})
    public void handleEvent(MotechEvent event) {
        final CallDetailRecord callDetailRecord = (CallDetailRecord) (event.getParameters().get("call_detail_record"));
        if (callDetailRecord != null) {
            callRecordsService.add(callDetailRecord);
        } else {
            log.error("Call detail record is missing:" + event);
        }
    }
}
