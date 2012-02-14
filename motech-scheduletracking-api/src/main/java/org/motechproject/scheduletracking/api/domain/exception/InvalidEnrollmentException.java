package org.motechproject.scheduletracking.api.domain.exception;

public class InvalidEnrollmentException extends RuntimeException {
    public InvalidEnrollmentException() {
        super("Entity is not currently enrolled into the schedule.");
    }
}
