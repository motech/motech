package org.motechproject.server.config.settings;

import java.util.Properties;

public interface MotechSettings {

    String AMQ_QUEUE_EVENTS = "queue.for.events";
    String AMQ_QUEUE_SCHEDULER = "queue.for.scheduler";
    String AMQ_BROKER_URL = "broker.url";
    String AMQ_MAX_REDELIVERIES = "maximumRedeliveries";
    String AMQ_REDELIVERY_DELAY_IN_MILLIS = "redeliveryDelayInMillis";
    String AMQ_CONCURRENT_CONSUMERS = "concurrentConsumers";
    String AMQ_MAX_CONCURRENT_CONSUMERS = "maxConcurrentConsumers";

    String GRAPHITE_URL = "graphite.url";

    String SCHEDULER_URL = "scheduler.url";

    String LANGUAGE = "system.language";
    String STATUS_MSG_TIMEOUT = "statusmsg.timeout";
    String SERVER_URL = "server.url";
    String UPLOAD_SIZE = "upload.size";

    String PROVIDER_NAME = "provider.name";
    String PROVIDER_URL = "provider.url";
    String LOGINMODE = "login.mode";

    String getLanguage();

    String getStatusMsgTimeout();

    String getLoginMode();

    String getProviderName();

    String getProviderUrl();

    String getServerUrl();

    String getServerHost();

    String getUploadSize();

    Properties getActivemqProperties();

    Properties getMetricsProperties();

    Properties getSchedulerProperties();
}
