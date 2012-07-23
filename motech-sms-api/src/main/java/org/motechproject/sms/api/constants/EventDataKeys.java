/**
 * \defgroup sms SMS
 */
/**
 * \ingroup sms
 * Constants used in SMS module
 */
package org.motechproject.sms.api.constants;

public final class EventDataKeys {
    /**
     * SendSMS event payload key indicating the recipients of the message
     */
    public static final String RECIPIENTS = "number";
    /**
     * SendSMS event payload key indicating the message text
     */
    public static final String MESSAGE = "message";
    /**
     * SendSMS event payload key indicating the scheduled time for message delivery
     */
    public static final String DELIVERY_TIME = "delivery_time";
    /**
     * Indicates the sender of the message
     */
    public static final String SENDER = "sender";
    /**
     * Indicates the message text for an inbound sms
     */
    public static final String INBOUND_MESSAGE = "inbound_message";
    /**
     * the timestamp when the recipient received the message
     */
    public static final String TIMESTAMP = "timestamp";

    private EventDataKeys() {
    }
}
