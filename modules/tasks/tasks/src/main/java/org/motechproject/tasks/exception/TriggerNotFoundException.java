package org.motechproject.tasks.exception;

/**
 * Thrown when requested trigger doesn't exists.
 */
public class TriggerNotFoundException extends Exception {

    /**
     * Exception constructor.
     *
     * @param message  the message to be passed with exception
     */
    public TriggerNotFoundException(String message) {
        super(message);
    }

}
