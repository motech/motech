package org.motechproject.sms.smpp.constants;

/**
 * describes all the events emitted by the module
 */
public class EventSubjects {
    /**
     * This event is raised when an sms remains undelivered after the delivery has been retried max_retries number of times.
     * Payload:
     * RECIPIENT - the reciepient for which the message delivery has failed
     * MESSAGE - the message text
     */
    public static final String SMS_FAILURE_NOTIFICATION = "sms_failure_notification";
    /**
     * This event is raised when the SMSC notifies the module of an inbound sms.
     * Payload:
     * SENDER - the sender's number
     * INBOUND_MESSAGE - the message text
     * TIMESTAMP - the time at which the SMS
     */
    public static final String INBOUND_SMS = "inbound_sms";
    public static final String SMS_DELIVERY_REPORT = "sms_delivery_report";
}
