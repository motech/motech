package org.motechproject.sms.http;

import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.EventKeys;

public class SMSSendHandler {

    @MotechListener(subjects = EventKeys.SEND_SMS)
    public void handle(){

    }
}
