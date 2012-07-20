package org.motechproject.server.config.settings;

import java.util.Properties;

public interface MotechSettings {
    public static final String DB_HOST = "db.host";
    public static final String DB_PORT = "db.port";
    public static final String DB_USERNAME = "db.user";
    public static final String DB_PASSWORD = "db.password";
    public static final String DB_MAX_CONNECTIONS = "db.maxConnections";
    public static final String DB_CONNECTION_TIMEOUT = "db.connectionTimeout";
    public static final String DB_SOCKET_TIMEOUT = "db.socketTimeout";

    public static final String AMQ_QUEUE_EVENTS = "queue.for.events";
    public static final String AMQ_QUEUE_SCHEDULER = "queue.for.scheduler";
    public static final String AMQ_BROKER_URL = "broker.url";
    public static final String AMQ_MAX_REDELIVERIES = "maximumRedeliveries";
    public static final String AMQ_REDELIVERY_DELAY_IN_MILLIS = "redeliveryDelayInMillis";
    public static final String AMQ_CONCURRENT_CONSUMERS = "concurrentConsumers";
    public static final String AMQ_MAX_CONCURRENT_CONSUMERS = "maxConcurrentConsumers";

    public static final String QUARTZ_SCHEDULER_NAME = "org.quartz.scheduler.instanceName";
    public static final String QUARTZ_THREAD_POOL_CLASS = "org.quartz.threadPool.class";
    public static final String QUARTZ_THREAD_POOL_THREAD_COUNT = "org.quartz.threadPool.threadCount";
    public static final String QUARTZ_JOB_STORE_CLASS = "org.quartz.jobStore.class";

    public static final String LANGUAGE = "system.language";

    String getLanguage();

    Properties getCouchDBProperties();

    Properties getActivemqProperties();

    Properties getQuartzProperties();
}
