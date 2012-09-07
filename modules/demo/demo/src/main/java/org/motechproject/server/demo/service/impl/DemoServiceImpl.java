package org.motechproject.server.demo.service.impl;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.server.demo.EventKeys;
import org.motechproject.server.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class DemoServiceImpl implements DemoService {
    @Autowired
    private MotechSchedulerService schedulerService;

    @Autowired
    private EventRelay eventRelay;

    @Override
    public void schedulePhoneCall(String phoneNumber, Date callTime) {
        RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(createMotechEvent(phoneNumber), callTime);
        schedulerService.scheduleRunOnceJob(schedulableJob);
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
