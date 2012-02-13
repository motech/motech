package org.motechproject.scheduletracking.api.domain.exception;

public class NoMoreMilestonesToFulfillException extends RuntimeException {
    public NoMoreMilestonesToFulfillException() {
        super("All milestones in the schedule have been fulfilled.");
    }
}
