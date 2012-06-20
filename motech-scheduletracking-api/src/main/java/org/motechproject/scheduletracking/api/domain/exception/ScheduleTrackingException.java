package org.motechproject.scheduletracking.api.domain.exception;

public class ScheduleTrackingException extends RuntimeException {
    public ScheduleTrackingException(String s, String ... args) {
        super(String.format(s, args));
    }
}
