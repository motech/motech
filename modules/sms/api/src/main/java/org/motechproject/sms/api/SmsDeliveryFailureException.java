package org.motechproject.sms.api;

public class SmsDeliveryFailureException extends Exception {

    public SmsDeliveryFailureException(Exception cause) {
        super(cause);
    }

    public SmsDeliveryFailureException() {
    }
}
