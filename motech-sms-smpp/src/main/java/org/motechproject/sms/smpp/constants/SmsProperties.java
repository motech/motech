package org.motechproject.sms.smpp.constants;

/**
 * general sms delivery properties not specific to smpp or http
 */
public final class SmsProperties {
    /**
     * maximum number of times that the delivery of a message must be retired before it is marked as failed.
     */
    public static final String MAX_RETRIES = "max_retries";

    private SmsProperties() {
    }
}
