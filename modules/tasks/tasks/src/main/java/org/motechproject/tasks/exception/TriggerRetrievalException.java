package org.motechproject.tasks.exception;

/**
 * Thrown when there were problems while retrieving trigger(s).
 */
public class TriggerRetrievalException extends RuntimeException {

    public TriggerRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }

}
