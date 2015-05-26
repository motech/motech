package org.motechproject.tasks.ex;

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
