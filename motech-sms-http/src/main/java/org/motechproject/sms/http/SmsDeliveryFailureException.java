package org.motechproject.sms.http;

public class SmsDeliveryFailureException extends Exception {

    public SmsDeliveryFailureException(Exception cause) {
        super(cause);
    }

    public SmsDeliveryFailureException() {
    }
}
