package org.motechproject.sms.api;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class SMSRequest {

    private String recipient;
    private String message;

    public SMSRequest() {
    }

    public SMSRequest(String message, String recipient) {
        this.message = message;
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public boolean isValid() {
        return isNotBlank(recipient) && isNotBlank(message);
    }
}
