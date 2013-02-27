package org.motechproject.sms.http;

import org.motechproject.sms.http.template.SmsHttpTemplate;

import static org.apache.commons.lang.StringUtils.isBlank;

public class SMSGatewayResponse {

    private final SmsHttpTemplate smsHttpTemplate;
    private final String response;

    public SMSGatewayResponse(SmsHttpTemplate template, String response) {
        this.smsHttpTemplate = template;
        this.response = response;
    }

    public boolean isSuccess() {

        if (isBlank(response)) {
            return false;
        }

        return response.matches(smsHttpTemplate.getSuccessfulResponsePattern());
    }
}
