package org.motechproject.sms.smpp.constants;

/**
 * properties related to the SMPP connection between the SMSC and the ESME
 */
public class SmppProperties {
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
}
