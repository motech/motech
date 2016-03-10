package org.motechproject.scheduler.exception;

import java.util.List;

/**
 * Thrown when error within MOTECH Scheduler occurs. Can be caused by updating non-existent job, job having invalid cron
 * expression etc.
 *
 * User: Igor (iopushnyev@2paths.com)
 * Date: 17/02/11
 * Time: 4:20 PM
 */
public class MotechSchedulerException extends RuntimeException {

    private String messageKey;

    private List<String> params;

    public MotechSchedulerException() {
        this(null, null);
    }

    public MotechSchedulerException(String message) {
        this(message, null);
    }

    public MotechSchedulerException(Throwable cause) {
        super(cause);
    }

    public MotechSchedulerException(String message, Throwable cause) {
        this(message, null, null, cause);
    }

    public MotechSchedulerException(String message, String messageKey, List<String> params) {
        this(message, messageKey, params, null);
    }

    public MotechSchedulerException(String message, String messageKey, List<String> params, Throwable cause) {
        super(message, cause);
        this.messageKey = messageKey;
        this.params = params;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public List<String> getParams() {
        return params;
    }
}
