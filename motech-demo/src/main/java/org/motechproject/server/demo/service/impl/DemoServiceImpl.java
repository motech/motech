package org.motechproject.server.demo.service.impl;

import org.motechproject.scheduler.context.EventContext;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.scheduler.event.EventRelay;
import org.motechproject.scheduler.gateway.MotechSchedulerGateway;
import org.motechproject.server.demo.EventKeys;
import org.motechproject.server.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DemoServiceImpl implements DemoService {
    @Autowired
    private MotechSchedulerGateway schedulerGateway;

    private EventRelay eventRelay = EventContext.getInstance().getEventRelay();

    @Override
    public void schedulePhoneCall(String phoneNumber, Date callTime) {
        RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(createMotechEvent(phoneNumber), callTime);
        schedulerGateway.scheduleRunOnceJob(schedulableJob);
    }

    @Override
    public void initiatePhoneCall(String phoneNumber) {
        eventRelay.sendEventMessage(createMotechEvent(phoneNumber));
    }

    private MotechEvent createMotechEvent(String phoneNumber) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.PHONE_KEY, phoneNumber);

        return new MotechEvent(EventKeys.CALL_EVENT_SUBJECT, parameters);
    }
}
