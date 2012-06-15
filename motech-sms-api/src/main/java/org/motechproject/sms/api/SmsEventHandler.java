package org.motechproject.sms.api;

import org.motechproject.scheduler.domain.MotechEvent;

public interface SmsEventHandler {
    public void handle(MotechEvent event) throws Exception;
}