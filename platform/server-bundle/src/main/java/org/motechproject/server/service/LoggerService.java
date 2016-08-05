package org.motechproject.server.service;

/**
 *  The <code>LoggerService</code> interface provides a method for logging messages to server log.
 */
public interface LoggerService {

    /**
     * Logs message to server log at picked level
     * @param level  the level at which message will be logged
     * @param message  the message to be logged
     */
    void logMessage(String level, String message);
}
