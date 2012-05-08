package org.motechproject.sms.http.constants;

public class EventSubjects {
    /**
     * Event key specifying an sms message is to be scheduled for delivery. A listener can receive this event at the scheduled time and handle message delivery.
     * Payload:
     * number - List<String> containing the recipients for the message
     * message - String containing the message text
     */
	public static final String SCHEDULED_SEND_SMS = "ScheduledSendSMS";
}
