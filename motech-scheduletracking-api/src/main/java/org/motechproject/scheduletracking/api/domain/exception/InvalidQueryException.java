package org.motechproject.scheduletracking.api.domain.exception;

public class InvalidQueryException extends RuntimeException {
    public InvalidQueryException(String s, String... args) {
        super(String.format(s, args));
    }
}
