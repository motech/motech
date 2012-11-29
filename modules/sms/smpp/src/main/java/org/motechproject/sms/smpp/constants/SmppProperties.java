package org.motechproject.sms.smpp.constants;

/**
 * properties related to the SMPP connection between the SMSC and the ESME
 */
public final class SmppProperties {
    /**
     * the hostname of the SMSC
     */
    public static final String HOST = "host";
    /**
     * the port where the SMSC is listening for SMPP messages
     */
    public static final String PORT = "port";
    /**
     * identifies the ESME requesting a connection to the SMSC
     */
    public static final String SYSTEM_ID = "system_id";
    /**
     * password to authenticate the ESME on the SMSC
     */
    public static final String PASSWORD = "password";
    /**
     * Flag to enable/ disable Delivery Reports for Outbound messages
     */
    public static final String DELIVERY_REPORTS = "delivery_reports";
    /**
     * Type used to bind to the target gateway.
     */
    public static final String BINDTYPE = "bind_type";
    /**
     * Bind Type Of Number
     */
    public static final String BIND_TON = "bind_ton";
    /**
     * Number plan indicator
     */
    public static final String BIND_NPI = "bind_npi";
    /**
     * Source type of number
     */
    public static final String SOURCE_TON = "source_ton";
    /**
     * Destination type of number
     */
    public static final String DESTINATION_TON = "dest_ton";
    /**
     * Source Numbering plan indicator
     */
    public static final String SOURCE_NPI = "source_npi";
    /**
     * Destination Numbering plan indicator
     */
    public static final String DESTINATION_NPI = "dest_npi";
    /**
     * Directory for queue manager to persist outbound messages
     */
    public static final String QUEUE_DIRECTORY = "queue_directory";

    private SmppProperties() {
    }
}
