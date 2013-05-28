package org.motechproject.sms.smpp.it;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;

import static org.motechproject.sms.api.constants.EventDataKeys.INBOUND_MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.SENDER;
import static org.motechproject.sms.api.constants.EventSubjects.INBOUND_SMS;

public class ReceiveSmsIT {

    @MotechListener(subjects = INBOUND_SMS)
    public void handle(MotechEvent event) {
        System.out.println(event.getParameters().get(INBOUND_MESSAGE));
        System.out.println(event.getParameters().get(SENDER));
    }
}