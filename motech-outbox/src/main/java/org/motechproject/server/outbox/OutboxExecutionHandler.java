package org.motechproject.server.outbox;

import org.motechproject.ivr.model.CallInitiationException;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.outbox.api.EventKeys;
import org.motechproject.scheduler.domain.CronJobId;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.gateway.MotechSchedulerGateway;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class OutboxExecutionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MotechSchedulerGateway schedulerGateway;

    @Autowired
    private IVRService ivrService;

    @Autowired
    private Properties outboxProperties;

    @MotechListener(subjects = {EventKeys.EXECUTE_OUTBOX_SUBJECT })
    public void execute(MotechEvent event) {

        String externalID = EventKeys.getExternalID(event);
        if (externalID == null) {
            logger.error("Can not handle Event: " + event.getSubject() +
                    ". The event is invalid - missing the " + EventKeys.EXTERNAL_ID_KEY + " parameter");
            return;
        }

        String phoneNumber = EventKeys.getPhoneNumberKey(event);
        if (phoneNumber == null) {
            logger.error("Can not handle Event: " + event.getSubject() +
                    ". The event is invalid - missing the " + EventKeys.PHONE_NUMBER_KEY + " parameter");
            return;
        }

        String language = EventKeys.getLanguageKey(event);
        if (language == null) {
            logger.error("Can not handle Event: " + event.getSubject() +
                    ". The event is invalid - missing the " + EventKeys.LANGUAGE_KEY + " parameter");
            return;
        }

        try {
            final int timeOut = 20000;
            String vxmlUrl = "http://" + outboxProperties.getProperty("outbox.host.ip")
                    + "/motech-platform-server/module/outbox/vxml/outboxMessage?pId=" + externalID + "&ln=" + language;
            CallRequest callRequest = new CallRequest(phoneNumber, timeOut, vxmlUrl);

            Map<String, Object> messageParameters = new HashMap<String, Object>();
            messageParameters.put(EventKeys.EXTERNAL_ID_KEY, externalID);
            MotechEvent incompleteEvent = new MotechEvent(EventKeys.INCOMPLETE_OUTBOX_CALL_SUBJECT,
                    messageParameters);

            callRequest.setOnBusyEvent(incompleteEvent);
            callRequest.setOnFailureEvent(incompleteEvent);
            callRequest.setOnNoAnswerEvent(incompleteEvent);

            MotechEvent successEvent = new MotechEvent(EventKeys.COMPLETED_OUTBOX_CALL_SUBJECT,
                    messageParameters);

            callRequest.setOnSuccessEvent(successEvent);

            ivrService.initiateCall(callRequest);
        } catch (CallInitiationException e) {
            logger.warn("Unable to initiate call to externalId=" + externalID + " e: " + e.getMessage());
        }
    }

    @MotechListener(subjects = {EventKeys.SCHEDULE_EXECUTION_SUBJECT })
    public void schedule(MotechEvent event) {

        Integer callHour = EventKeys.getCallHourKey(event);
        if (callHour == null) {
            logger.error("Can not handle Event: " + event.getSubject() +
                    ". The event is invalid - missing the " + EventKeys.CALL_HOUR_KEY + " parameter");
            return;
        }

        Integer callMinute = EventKeys.getCallMinuteKey(event);
        if (callMinute == null) {
            logger.error("Can not handle Event: " + event.getSubject() +
                    ". The event is invalid - missing the " + EventKeys.CALL_MINUTE_KEY + " parameter");
            return;
        }

        String externalID = EventKeys.getExternalID(event);
        if (externalID == null) {
            logger.error("Can not handle Event: " + event.getSubject() +
                    ". The event is invalid - missing the " + EventKeys.EXTERNAL_ID_KEY + " parameter");
            return;
        }

        String phoneNumber = EventKeys.getPhoneNumberKey(event);
        if (phoneNumber == null) {
            logger.error("Can not handle Event: " + event.getSubject() +
                    ". The event is invalid - missing the " + EventKeys.PHONE_NUMBER_KEY + " parameter");
            return;
        }

        MotechEvent reminderEvent = new MotechEvent(EventKeys.EXECUTE_OUTBOX_SUBJECT, event.getParameters());
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(reminderEvent, String.format("0 %d %d * * ?", callMinute, callHour));

        schedulerGateway.scheduleJob(cronSchedulableJob);
    }

    @MotechListener(subjects = {EventKeys.UNSCHEDULE_EXECUTION_SUBJECT })
    public void unschedule(MotechEvent event) {
        if (EventKeys.getScheduleJobIdKey(event) == null) {
            logger.error(String.format("Can not handle Event: %s. The event is invalid - missing the %s parameter",
                    event.getSubject(), EventKeys.SCHEDULE_JOB_ID_KEY));

            return;
        }

        schedulerGateway.unscheduleJob(new CronJobId(event));
    }
}
