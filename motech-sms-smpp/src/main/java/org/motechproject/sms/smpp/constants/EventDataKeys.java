package org.motechproject.sms.smpp.constants;

public class EventDataKeys {
    /**
     * Indicates the sender of the message
     */
    public static final String SENDER = "sender";
    /**
     * Indicates the recipient for the message
     */
    public static final String RECIPIENT = "recipient";
    /**
     * Indicates the message text for an inbound sms
     */
    public static final String INBOUND_MESSAGE = "inbound_message";
    /**
     * the timestamp when the sender sent out the message
     */
    public static final String TIMESTAMP = "timestamp";
}