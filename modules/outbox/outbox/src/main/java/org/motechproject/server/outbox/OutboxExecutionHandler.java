package org.motechproject.server.outbox;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ivr.model.CallInitiationException;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.outbox.api.EventKeys;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronJobId;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;

/**
 *
 */
public class OutboxExecutionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MotechSchedulerService motechSchedulerService;

    @Autowired
    private IVRService ivrService;

    @Autowired
    private Properties outboxProperties;

    @MotechListener(subjects = {EventKeys.EXECUTE_OUTBOX_SUBJECT})
    public void execute(MotechEvent event) {

        String externalID = EventKeys.getExternalID(event);
        if (externalID == null) {
            logError(event, EventKeys.EXTERNAL_ID_KEY);
            return;
        }

        String phoneNumber = EventKeys.getPhoneNumberKey(event);
        if (phoneNumber == null) {
            logError(event, EventKeys.PHONE_NUMBER_KEY);
            return;
        }

        String language = EventKeys.getLanguageKey(event);
        if (language == null) {
            logError(event, EventKeys.LANGUAGE_KEY);
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
            logger.warn(format("Unable to initiate call to externalId=%s e: %s", externalID, e.getMessage()));
        }
    }

    @MotechListener(subjects = {EventKeys.SCHEDULE_EXECUTION_SUBJECT})
    public void schedule(MotechEvent event) {

        Integer callHour = EventKeys.getCallHourKey(event);
        if (callHour == null) {
            logError(event, EventKeys.CALL_HOUR_KEY);
            return;
        }

        Integer callMinute = EventKeys.getCallMinuteKey(event);
        if (callMinute == null) {
            logError(event, EventKeys.CALL_MINUTE_KEY);
            return;
        }

        String externalID = EventKeys.getExternalID(event);
        if (externalID == null) {
            logError(event, EventKeys.EXTERNAL_ID_KEY);
            return;
        }

        String phoneNumber = EventKeys.getPhoneNumberKey(event);
        if (phoneNumber == null) {
            logError(event, EventKeys.PHONE_NUMBER_KEY);
            return;
        }

        MotechEvent reminderEvent = new MotechEvent(EventKeys.EXECUTE_OUTBOX_SUBJECT, event.getParameters());
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(reminderEvent, format("0 %d %d * * ?", callMinute, callHour));

        motechSchedulerService.scheduleJob(cronSchedulableJob);
    }

    @MotechListener(subjects = {EventKeys.UNSCHEDULE_EXECUTION_SUBJECT})
    public void unschedule(MotechEvent event) {
        if (EventKeys.getScheduleJobIdKey(event) == null) {
            logError(event, EventKeys.SCHEDULE_JOB_ID_KEY);
            return;
        }

        motechSchedulerService.unscheduleJob(new CronJobId(event));
    }

    private void logError(MotechEvent event, String externalIdKey) {
        logger.error(format("Can not handle Event: %s. The event is invalid - missing the %s parameter", event.getSubject(), externalIdKey));
    }
}
