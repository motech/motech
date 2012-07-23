/**
 * \defgroup smpp SMS SMPP
 */
/**
 * \ingroup smpp
 * Constants used in SMS SMPP module
 */
package org.motechproject.sms.smpp.constants;

public final class EventDataKeys {
    /**
     * Indicates the recipient for the message
     */
    public static final String RECIPIENT = "recipient";
    /**
     * Indicates the message text for an inbound sms
     */
    public static final String STATUS_MESSAGE = "status_message";

    private EventDataKeys() {
    }
}
