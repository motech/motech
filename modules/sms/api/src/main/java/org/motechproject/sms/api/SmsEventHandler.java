package org.motechproject.sms.api;

import org.motechproject.event.MotechEvent;

public interface SmsEventHandler {
    void handle(MotechEvent event) throws SmsDeliveryFailureException;
}
