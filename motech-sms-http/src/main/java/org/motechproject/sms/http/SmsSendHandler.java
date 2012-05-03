package org.motechproject.sms.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.SmsEventHandler;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SmsSendHandler implements SmsEventHandler {

    private SmsHttpTemplate template;
    private HttpClient commonsHttpClient;
    private static Logger log = LoggerFactory.getLogger(SmsSendHandler.class);

    @Autowired
    public SmsSendHandler(TemplateReader templateReader, HttpClient commonsHttpClient) {
        String templateFile = "/sms-http-template.json";
        this.template = templateReader.getTemplate(templateFile);
        this.commonsHttpClient = commonsHttpClient;
    }

    @Override
    @MotechListener(subjects = EventSubjects.SEND_SMS)
    public void handle(MotechEvent event) throws IOException, SmsDeliveryFailureException {
        List<String> recipients = (List<String>) event.getParameters().get(EventDataKeys.RECIPIENTS);
        String message = (String) event.getParameters().get(EventDataKeys.MESSAGE);
        HttpMethod httpMethod = template.generateRequestFor(recipients, message);
        int status = commonsHttpClient.executeMethod(httpMethod);
        String response = httpMethod.getResponseBodyAsString();
        log.info("HTTP Status:" + status + "|Response:" + response);

        if (response != null && !response.contains(template.getOutgoing().getResponse().getSuccess())) {
            log.info("delivery failed, retrying...");
            throw new SmsDeliveryFailureException();
        }
    }
}
