package org.motechproject.sms.api;

public class SmsDeliveryFailureException extends Exception {

    public SmsDeliveryFailureException(Exception cause) {
        super(cause);
    }

    public SmsDeliveryFailureException() {
    }

    public SmsDeliveryFailureException(String message) {
        super(message);
    }

    public SmsDeliveryFailureException(String message, Exception cause) {
        super(message, cause);
    }
}
