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
    public static final String SEND_SMSDT = "SendSMSDT";

    /**
     * Event key specifying an sms message is to be delivered. A listener can receive this event and handle message delivery.
     * Payload:
     * sender - String containing the sender of the message
     * message - String containing the message text
     * timestamp - String specifying the timestamp when the recipient received the message
     */
    public static final String INBOUND_SMS = "inbound_sms";

    /**
     * This event is raised when an sms remains undelivered after the delivery has been retried max_retries number of times.
     * Payload:
     * RECIPIENT - the recipient for which the message delivery has failed
     * MESSAGE - the message text
     */
    public static final String SMS_FAILURE_NOTIFICATION = "sms_failure_notification";

    /**
     * This event is raised when the SMSC notifies the module with the delivery status of an outbound sms.
     * Payload:
     * SENDER - the sender's number
     * STATUS_MESSAGE - the status for delivery of the outbound message
     * TIMESTAMP - the time at which the SMS
     */
    public static final String SMS_DELIVERY_REPORT = "sms_delivery_report";

    private EventSubjects() {
    }
}
