package org.motechproject.config.core;

/**
 * The object of this class is thrown when there is a problem with reading
 * the configuration from the predefined sources.
 **/
public class MotechConfigurationException extends RuntimeException {
    /**
     *
     * @param message A descriptive message explaining the nature of the problem resulted in exception
     * @param exception Actual exception (if any) that this exception resulted from
     */
    public MotechConfigurationException(String message, Exception exception) {
        super(message, exception);
    }

    public MotechConfigurationException(String message) {
        super(message);
    }
}
