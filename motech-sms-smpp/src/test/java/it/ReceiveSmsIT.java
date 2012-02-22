package it;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.smpp.constants.EventDataKeys;
import org.motechproject.sms.smpp.constants.EventSubjects;

public class ReceiveSmsIT {

    @MotechListener(subjects = EventSubjects.INBOUND_SMS)
    public void handle(MotechEvent event) {
        System.out.println(event.getParameters().get(EventDataKeys.INBOUND_MESSAGE));
        System.out.println(event.getParameters().get(EventDataKeys.SENDER));
    }
}