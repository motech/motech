package org.motechproject.scheduler.exception;

/**
 * Indicates an error when retrieving scheduled jobs from the database.
 */
public class MotechSchedulerJobRetrievalException extends RuntimeException {


    private static final long serialVersionUID = -2109975306496379841L;

    public MotechSchedulerJobRetrievalException() {
    }

    public MotechSchedulerJobRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
