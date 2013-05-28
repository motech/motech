package org.motechproject.sms.http.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.SmsDeliveryFailureException;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.motechproject.sms.http.SMSGatewayResponse;
import org.motechproject.sms.http.TemplateReader;
import org.motechproject.sms.http.event.SendSmsDTEvent;
import org.motechproject.sms.http.template.Authentication;
import org.motechproject.sms.http.template.SmsHttpTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.motechproject.sms.api.DeliveryStatus.ABORTED;
import static org.motechproject.sms.api.DeliveryStatus.DELIVERED;
import static org.motechproject.sms.api.DeliveryStatus.KEEPTRYING;
import static org.motechproject.sms.api.SMSType.OUTBOUND;
import static org.motechproject.sms.api.constants.EventDataKeys.FAILURE_COUNT;
import static org.motechproject.sms.api.constants.EventDataKeys.MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.RECIPIENT;
import static org.motechproject.sms.api.constants.EventSubjects.SMS_FAILURE_NOTIFICATION;

@Service
public class SmsHttpService {
    private static Logger log = LoggerFactory.getLogger(SmsHttpService.class);

    private EventRelay eventRelay;
    private HttpClient commonsHttpClient;
    private MotechSchedulerService schedulerService;
    private SmsAuditService smsAuditService;
    private TemplateReader templateReader;

    private Random random = new Random();
    private final Integer maxRetries;

    @Autowired
    public SmsHttpService(EventRelay eventRelay, HttpClient commonsHttpClient, MotechSchedulerService schedulerService,
                          @Qualifier("smsApiSettings") SettingsFacade settings, SmsAuditService smsAuditService,
                          TemplateReader templateReader) {
        this.eventRelay = eventRelay;
        this.commonsHttpClient = commonsHttpClient;
        this.schedulerService = schedulerService;
        this.smsAuditService = smsAuditService;
        this.templateReader = templateReader;

        String maxRetriesAsString = settings.getProperty("max_retries");
        this.maxRetries = maxRetriesAsString != null ? Integer.parseInt(maxRetriesAsString) : 0;
    }

    public void sendSms(List<String> recipients, String message) throws SmsDeliveryFailureException {
        sendSms(recipients, message, 0);
    }

    public void sendSms(List<String> recipients, String message, Integer failureCount) throws SmsDeliveryFailureException {
        if (CollectionUtils.isEmpty(recipients) || StringUtils.isEmpty(message)) {
            throw new IllegalArgumentException("Recipients or Message should not be empty");
        }

        String response = null;
        HttpMethod httpMethod = null;
        SmsHttpTemplate smsHttpTemplate = template();
        DateTime sendTime = DateUtil.now();

        try {
            httpMethod = smsHttpTemplate.generateRequestFor(recipients, message);
            setAuthenticationInfo(smsHttpTemplate.getAuthentication());

            int status = commonsHttpClient.executeMethod(httpMethod);
            response = httpMethod.getResponseBodyAsString();

            log.info("HTTP Status:" + status + "|Response:" + response);
        } catch (Exception e) {
            log.error("SMSDeliveryFailure due to : ", e);

            if (failureCount >= maxRetries) {
                addSmsRecord(recipients, message, sendTime, ABORTED);
            } else {
                addSmsRecord(recipients, message, sendTime, KEEPTRYING);
            }

            raiseFailureEvent(recipients, message, failureCount);

            return;
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }

        if (!new SMSGatewayResponse(template(), response).isSuccess()) {
            log.error(String.format("SMS delivery failed. Retrying...; Response: %s", response));
            addSmsRecord(recipients, message, sendTime, KEEPTRYING);
            raiseFailureEvent(recipients, message, failureCount);
        } else {
            log.debug("SMS with message %s sent successfully to %s:", message, StringUtils.join(recipients.iterator(), ","));
            addSmsRecord(recipients, message, sendTime, DELIVERED);
        }
    }

    public void sendSms(List<String> recipients, String message, DateTime deliveryTime) throws SmsDeliveryFailureException {
        sendSms(recipients, message, 0, deliveryTime);
    }

    public void sendSms(List<String> recipients, String message, Integer failureCount, DateTime deliveryTime) throws SmsDeliveryFailureException {
        RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(new SendSmsDTEvent(recipients, message, failureCount).toMotechEvent(), deliveryTime.toDate());
        schedulerService.safeScheduleRunOnceJob(schedulableJob);

        log.info(String.format("Scheduling message [%s] to number %s at %s.", message, recipients, deliveryTime.toString()));
    }

    private void setAuthenticationInfo(Authentication authentication) {
        if (authentication == null) {
            return;
        }

        commonsHttpClient.getParams().setAuthenticationPreemptive(true);
        commonsHttpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(authentication.getUsername(), authentication.getPassword()));
    }

    //Recreating the template for every request so that latest templates can be changed
    private SmsHttpTemplate template() {
        return templateReader.getTemplate();
    }

    private void raiseFailureEvent(List<String> recipients, String message, int failureCount) {
        for (String recipient : recipients) {
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(RECIPIENT, recipient);
            parameters.put(MESSAGE, message);
            parameters.put(FAILURE_COUNT, failureCount + 1);
            eventRelay.sendEventMessage(new MotechEvent(SMS_FAILURE_NOTIFICATION, parameters));
        }
    }

    private void addSmsRecord(List<String> recipients, String message, DateTime sendTime, DeliveryStatus deliveryStatus) {
        for (String recipient : recipients) {
            smsAuditService.log(new SmsRecord(
                    OUTBOUND, recipient, message, sendTime, deliveryStatus,
                    Integer.toString(Math.abs(random.nextInt()))
            ));
        }
    }
}
