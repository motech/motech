package org.motechproject.commons.api;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LayerSupertype for entire motech project. Used optionally through the motech project.
 */
public class MotechObject {
    private Logger logger;

    /**
     * Returns logger for this object. If this doesn't have one, it is created and then returned.
     *
     * @return the logger for this
     */
    protected Logger logger() {
        if (logger == null) {
            logger = LoggerFactory.getLogger(this.getClass());
        }
        return logger;
    }

    /**
     * Logs given information.
     *
     * @param templateMessage  the template for message
     * @param params  arguments to be merged into message
     */
    protected void logInfo(String templateMessage, Object ... params) {
        logger().info(String.format(templateMessage, params));
    }

    /**
     * Logs given error.
     *
     * @param templateMessage  the template for message
     * @param params  arguments to be merged into message
     */
    protected void logError(String templateMessage, Object ... params) {
        logger().error(String.format(templateMessage, params));
    }

    /**
     * Logs given error.
     *
     * @param message  the message to be logged
     */
    protected void logError(String message) {
        logger().error(message);
    }

    /**
     * Log an exception with accompanying message.
     *
     * @param message  the message accompanying the exception
     * @param e  exception to log
     */
    protected void logError(String message, Exception e) {
        logger().error(message, e);
    }

    /**
     * Asserts that given argument isn't null. Logs error when argument is null.
     *
     * @param objectName  the name of given object
     * @param object  the object to be checked
     * @throws IllegalArgumentException if {@code object} is null
     */
    protected void assertArgumentNotNull(String objectName, Object object) {
        if (object == null) {
            String message = String.format("%s cannot be null", objectName);
            logError(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Asserts that given argument isn't empty. Logs error when argument is empty.
     *
     * @param objectName  the name of given object
     * @param argument  the object to be checked
     * @throws IllegalArgumentException if {@code object} is empty
     */
    protected void assertArgumentNotEmpty(String objectName, String argument) {
        if (StringUtils.isEmpty(argument)) {
            String message = String.format("%s cannot be empty", objectName);
            logError(message);
            throw new IllegalArgumentException(message);
        }
    }
}
