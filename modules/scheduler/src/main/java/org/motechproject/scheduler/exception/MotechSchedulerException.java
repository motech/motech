package org.motechproject.scheduler.exception;

/**
 * User: Igor (iopushnyev@2paths.com)
 * Date: 17/02/11
 * Time: 4:20 PM
 */
public class MotechSchedulerException extends RuntimeException {

    public MotechSchedulerException() {
    }

    public MotechSchedulerException(String message) {
        super(message);
    }

    public MotechSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MotechSchedulerException(Throwable cause) {
        super(cause);
    }
}
