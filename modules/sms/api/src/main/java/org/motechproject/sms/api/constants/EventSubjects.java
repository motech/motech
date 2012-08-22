package org.motechproject.sms.api.constants;

public final class EventSubjects {
    /**
     * Event key specifying an sms message is to be delivered. A listener can receive this event and handle message delivery.
     * Payload:
     * number - List<String> containing the recipients for the message
     * message - String containing the message text
     * delivery_time - org.joda.DateTime used to schedule the delivery of the message at the specified time
     */
    public static final String SEND_SMS = "SendSMS";
    /**
     * Event key specifying an sms message is to be delivered. A listener can receive this event and handle message delivery.
     * Payload:
     * sender - String containing the sender of the message
     * message - String containing the message text
     * timestamp - String specifying the timestamp when the recipient received the message
     */
    public static final String INBOUND_SMS = "inbound_sms";

    private EventSubjects() {
    }
}
