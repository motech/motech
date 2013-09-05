package org.motechproject.scheduler.exception;

/**
 * The <code>SchedulerShutdownException</code> exception informs about that there were problems
 * with shutdown scheduler.
 */
public class SchedulerShutdownException extends RuntimeException {

    public SchedulerShutdownException(Throwable e) {
        super(e);
    }

}
