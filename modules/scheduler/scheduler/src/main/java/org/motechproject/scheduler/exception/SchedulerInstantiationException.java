package org.motechproject.scheduler.exception;

/**
 * Thrown when scheduler can't be instantiated.
 */
public class SchedulerInstantiationException extends RuntimeException {

    public SchedulerInstantiationException(String message, Throwable e) {
        super(message, e);
    }
}
