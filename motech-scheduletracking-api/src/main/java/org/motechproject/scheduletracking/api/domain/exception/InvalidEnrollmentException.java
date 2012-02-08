package org.motechproject.scheduletracking.api.domain.exception;

public class InvalidEnrollmentException extends RuntimeException {

    public InvalidEnrollmentException(String s, String ... args) {
        super(String.format(s, args));
    }
}
