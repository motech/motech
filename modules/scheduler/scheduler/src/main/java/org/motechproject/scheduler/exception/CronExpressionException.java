package org.motechproject.scheduler.exception;

/**
 * Thrown when the given String is not a valid cron expression.
 */
public class CronExpressionException extends RuntimeException {

    private static final String MESSAGE = "\"%s\" is not a valid cron expression.";

    public CronExpressionException(String expression) {
        super(String.format(MESSAGE, expression));
    }
}
