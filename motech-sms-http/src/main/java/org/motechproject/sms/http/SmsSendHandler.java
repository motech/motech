package org.motechproject.sms.http;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.EventKeys;

public class SmsSendHandler {

    @MotechListener(subjects = EventKeys.SEND_SMS)
    public void handle(MotechEvent event){

    }
}

