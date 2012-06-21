/**
 * \defgroup smpp SMS SMPP
 */
/**
 * \ingroup smpp
 * Constants used in SMS SMPP module
 */
package org.motechproject.sms.smpp.constants;

public class EventDataKeys {

    //TODO: move common fields to sms api

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
     * Indicates the message text for an inbound sms
     */
    public static final String STATUS_MESSAGE = "status_message";
    /**
     * the timestamp when the sender sent out the message
     */
    public static final String TIMESTAMP = "timestamp";
}