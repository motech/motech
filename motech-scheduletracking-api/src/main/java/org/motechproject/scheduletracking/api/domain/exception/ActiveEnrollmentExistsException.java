package org.motechproject.scheduletracking.api.domain.exception;

public class ActiveEnrollmentExistsException extends RuntimeException {

    public ActiveEnrollmentExistsException(String s, String ... args) {
        super(String.format(s, args));
    }
}
