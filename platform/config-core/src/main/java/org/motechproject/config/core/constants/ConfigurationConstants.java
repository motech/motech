package org.motechproject.config.core.constants;

/**
 * Provides all the configuration constants.
 */
public final class ConfigurationConstants {
    public static final String SETTINGS_FILE_NAME = "motech-settings.conf";

    public static final String AMQ_QUEUE_EVENTS = "jms.queue.for.events";
    public static final String AMQ_QUEUE_SCHEDULER = "jms.queue.for.scheduler";
    public static final String AMQ_BROKER_URL = "jms.broker.url";
    public static final String AMQ_MAX_REDELIVERIES = "jms.maximumRedeliveries";
    public static final String AMQ_REDELIVERY_DELAY_IN_MILLIS = "jms.redeliveryDelayInMillis";
    public static final String AMQ_CONCURRENT_CONSUMERS = "jms.concurrentConsumers";
    public static final String AMQ_MAX_CONCURRENT_CONSUMERS = "jms.maxConcurrentConsumers";

    public static final String LANGUAGE = "system.language";
    public static final String STATUS_MSG_TIMEOUT = "statusmsg.timeout";
    public static final String SERVER_URL = "server.url";
    public static final String UPLOAD_SIZE = "upload.size";

    public static final String PROVIDER_NAME = "provider.name";
    public static final String PROVIDER_URL = "provider.url";
    public static final String LOGINMODE = "login.mode";

    public static final String[] SUPPORTED_FILE_EXTNS = new String[]{"properties", "json"};

    private static final String BASE_SUBJECT = "org.motechproject.server.config.file.";
    public static final String FILE_DELETED_EVENT_SUBJECT = BASE_SUBJECT + "deleted";
    public static final String FILE_CHANGED_EVENT_SUBJECT = BASE_SUBJECT + "changed";
    public static final String FILE_CREATED_EVENT_SUBJECT = BASE_SUBJECT + "created";

    private ConfigurationConstants() {
    }
}
