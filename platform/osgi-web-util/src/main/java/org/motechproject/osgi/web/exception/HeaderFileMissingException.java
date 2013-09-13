package org.motechproject.osgi.web.exception;

public class HeaderFileMissingException extends RuntimeException {
    public HeaderFileMissingException(String message, Exception e) {
        super(message, e);
    }
}
