package org.motechproject.http.agent.domain;

public final class EventDataKeys {

    private EventDataKeys() {
        //static utility class
    }

    public static final String DATA = "data";
    public static final String URL = "url";
    public static final String METHOD = "method";
    public static final String RETRY_COUNT = "retry_count";
    public static final String RETRY_INTERVAL = "retry_interval";
}
