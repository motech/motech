package org.motechproject.server.config.settings;

import java.util.Properties;

public interface MotechSettings {
    String DB_HOST = "db.host";
    String DB_PORT = "db.port";
    String DB_USERNAME = "db.user";
    String DB_PASSWORD = "db.password";
    String DB_MAX_CONNECTIONS = "db.maxConnections";
    String DB_CONNECTION_TIMEOUT = "db.connectionTimeout";
    String DB_SOCKET_TIMEOUT = "db.socketTimeout";

    String AMQ_QUEUE_EVENTS = "queue.for.events";
    String AMQ_QUEUE_SCHEDULER = "queue.for.scheduler";
    String AMQ_BROKER_URL = "broker.url";
    String AMQ_MAX_REDELIVERIES = "maximumRedeliveries";
    String AMQ_REDELIVERY_DELAY_IN_MILLIS = "redeliveryDelayInMillis";
    String AMQ_CONCURRENT_CONSUMERS = "concurrentConsumers";
    String AMQ_MAX_CONCURRENT_CONSUMERS = "maxConcurrentConsumers";

    String QUARTZ_SCHEDULER_NAME = "org.quartz.scheduler.instanceName";
    String QUARTZ_THREAD_POOL_CLASS = "org.quartz.threadPool.class";
    String QUARTZ_THREAD_POOL_THREAD_COUNT = "org.quartz.threadPool.threadCount";
    String QUARTZ_JOB_STORE_CLASS = "org.quartz.jobStore.class";

    String LANGUAGE = "system.language";

    String getLanguage();

    Properties getCouchDBProperties();

    Properties getActivemqProperties();

    Properties getQuartzProperties();
}
