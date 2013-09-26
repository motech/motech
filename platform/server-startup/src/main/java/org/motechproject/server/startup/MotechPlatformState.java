package org.motechproject.server.startup;

public enum MotechPlatformState {
    STARTUP, NEED_BOOTSTRAP_CONFIG, NEED_CONFIG, FIRST_RUN, NORMAL_RUN, NO_DB, DB_ERROR
}
