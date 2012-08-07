package org.motechproject.eventlogging.service;

/**
 * A logging service manager that allows for register logging services. This class is exposed as an OSGi service to support cross bundle registration.
 */
public interface EventLoggingServiceManager {

    /**
     * Register a logging service in the platform, which will generate listeners for that logging service based on the subjects it listens on.
     * @param eventLoggingService The logging service to add to the platform.
     */
    void registerEventLoggingService(EventLoggingService eventLoggingService);

    /**
     * Not yet implemented
     * @param eventLoggingService
     */
    void updateEventLoggingService(EventLoggingService eventLoggingService);
}
