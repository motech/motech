package org.motechproject.config.core.constants;

/**
 * Provides all the configuration constants.
 */
public final class ConfigurationConstants {
    public static final String CONFIG_MODULE_DIR_PREFIX = "org.motechproject.";
    public static final String SETTINGS_FILE_NAME = "motech-settings.properties";

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

    public static final String JMX_HOST = "jmx.host";
    public static final String JMX_BROKER = "jmx.broker";
    public static final String JMX_USERNAME = "jmx.username";
    public static final String JMX_PASSWORD = "jmx.password";

    public static final String PROVIDER_NAME = "provider.name";
    public static final String PROVIDER_URL = "provider.url";
    public static final String LOGINMODE = "login.mode";

    public static final String PROPERTIES_EXTENSION = "properties";
    public static final String JSON_EXTENSION = "json";
    public static final String RAW_DIR = "raw";

    private static final String BASE_SUBJECT = "org.motechproject.server.config.";
    public static final String FILE_DELETED_EVENT_SUBJECT = BASE_SUBJECT + "file.deleted";
    public static final String FILE_CHANGED_EVENT_SUBJECT = BASE_SUBJECT + "file.changed";
    public static final String FILE_CREATED_EVENT_SUBJECT = BASE_SUBJECT + "file.created";
    public static final String BUNDLE_SETTINGS_CHANGED_EVENT_SUBJECT =  BASE_SUBJECT + "bundle.settings.changed";
    public static final String PLATFORM_SETTINGS_CHANGED_EVENT_SUBJECT = BASE_SUBJECT + "platform.settings.changed";

    public static final String FILE_PATH = "file.path";
    public static final String BUNDLE_ID = "bundle.id";
    public static final String BUNDLE_SYMBOLIC_NAME = "bundle.symbolic.name";
    public static final String BUNDLE_SECTION = "bundle.section";
    public static final String SETTINGS = "settings";

    public static final String EVENT_RELAY_CLASS_NAME = "org.motechproject.event.listener.EventRelay";
    public static final String MOTECH_EVENT_CLASS_NAME = "org.motechproject.event.MotechEvent";

    public static final String EMAIL_REQUIRED = "security.required.email";
    public static final String SESSION_TIMEOUT = "security.session.timeout";
    public static final String PASSWORD_VALIDATOR = "security.password.validator";
    public static final String MIN_PASSWORD_LENGTH = "security.password.minlength";
    public static final String FAILURE_LOGIN_LIMIT = "security.failure.login.limit";
    public static final String PASSWORD_RESET_DAYS = "security.password.reset.days";
    public static final String PASSWORD_REMINDER = "security.password.reminder.sendReminder";
    public static final String PASSWORD_REMINDER_DAYS = "security.password.reminder.daysBeforeExpiration";

    public static final String DATANUCLEUS_DATA_SETTINGS_FILE_NAME = "datanucleus_data.properties";
    public static final String DATANUCLEUS_SCHEMA_SETTINGS_FILE_NAME = "datanucleus_schema.properties";
    public static final String FLYWAY_DATA_SETTINGS_FILE_NAME = "flyway_data.properties";
    public static final String FLYWAY_SCHEMA_SETTINGS_FILE_NAME = "flyway_schema.properties";
    public static final String BOOTSTRAP_CONFIG_FILE_NAME = "bootstrap.properties";
    public static final String AMQ_DEFAULT_SETTINGS_FILE_NAME = "activemq-default.properties";

    /**
     * This is an utility class and should not be instantiated.
     */
    private ConfigurationConstants() {
    }
}
