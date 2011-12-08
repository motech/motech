package org.motechproject.sms.http;

import org.apache.commons.httpclient.HttpClient;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.EventKeys;
import org.springframework.stereotype.Component;

@Component
public class SmsSendHandler {
    public static final String SMS_HTTP_TEMPLATE_FILE = "/sms-http-template.json";

    private SmsSendTemplate template;
    private HttpClient httpClient;

    public SmsSendHandler() {
        template = new TemplateReader(SMS_HTTP_TEMPLATE_FILE).getTemplate();
    }

    @MotechListener(subjects = EventKeys.SEND_SMS)
    public void handle(MotechEvent event){

    }
}

