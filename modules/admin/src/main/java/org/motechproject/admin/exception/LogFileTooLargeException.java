package org.motechproject.admin.exception;

/**
 * Signals that the log file is too big to return to the caller.
 */
public class LogFileTooLargeException extends Exception {

    private static final long serialVersionUID = -7558282708410271782L;

    public LogFileTooLargeException(String message) {
        super(message);
    }
}
