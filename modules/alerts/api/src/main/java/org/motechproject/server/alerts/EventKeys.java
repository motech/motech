package org.motechproject.server.alerts;

public final class EventKeys {
    private EventKeys() { }

    public static final String EXTERNAL_ID_KEY = "ExternalId";
    public static final String ALERT_ID = "AlertId";
    public static final String ALERT_NAME = "AlertName";
    public static final String ALERT_TYPE = "AlertType";
    public static final String ALERT_DATE_TIME = "AlertDateTime";
    public static final String ALERT_PRIORITY = "AlertPriority";
    public static final String ALERT_STATUS = "AlertStatus";
    public static final String ALERT_DESCRIPTION = "AlertDescription";
    public static final String ALERT_DATA = "AlertData";
    public static final String BASE_SUBJECT = "org.motechproject.alerts.api.";
    public static final String CREATE_ALERT_SUBJECT = BASE_SUBJECT + "Create.Alert";
    public static final String CLOSE_ALERT_SUBJECT= BASE_SUBJECT + "Close.Alert";
    public static final String MARK_ALERT_READ_SUBJECT= BASE_SUBJECT + "Mark.Alert.Read";
}
