package org.motechproject.server.startup;

/**
 * Defines the different states of the MOTECH system.
 */
public enum MotechPlatformState {
    STARTUP, NEED_BOOTSTRAP_CONFIG, NEED_CONFIG, FIRST_RUN, NORMAL_RUN, NO_DB, DB_ERROR
}
