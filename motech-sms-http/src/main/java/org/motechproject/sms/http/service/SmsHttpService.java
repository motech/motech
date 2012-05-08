package org.motechproject.sms.http.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.motechproject.sms.http.SmsDeliveryFailureException;
import org.motechproject.sms.http.SmsHttpTemplate;
import org.motechproject.sms.http.TemplateReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SmsHttpService {

    private SmsHttpTemplate template;
    private HttpClient commonsHttpClient;
    private static Logger log = LoggerFactory.getLogger(SmsHttpService.class);

    @Autowired
    public SmsHttpService(TemplateReader templateReader, HttpClient commonsHttpClient) {
        String templateFile = "/sms-http-template.json";
        this.template = templateReader.getTemplate(templateFile);
        this.commonsHttpClient = commonsHttpClient;
    }

    public void sendSms(List<String> recipients, String message) throws SmsDeliveryFailureException {
        String response;
        try {
            HttpMethod httpMethod = template.generateRequestFor(recipients, message);
            int status = commonsHttpClient.executeMethod(httpMethod);
            response = httpMethod.getResponseBodyAsString();
            log.info("HTTP Status:" + status + "|Response:" + response);
        }
        catch (Exception e) {
            throw new SmsDeliveryFailureException();
        }
        if (response == null || !response.toLowerCase().contains(template.getOutgoing().getResponse().getSuccess().toLowerCase())) {
            log.info("delivery failed, retrying...");
            throw new SmsDeliveryFailureException();
        }
    }
}
