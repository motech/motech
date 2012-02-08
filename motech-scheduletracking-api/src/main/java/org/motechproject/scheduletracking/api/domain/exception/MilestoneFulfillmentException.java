package org.motechproject.scheduletracking.api.domain.exception;

public class MilestoneFulfillmentException extends RuntimeException {

    public MilestoneFulfillmentException(String s, String ... args) {
        super(String.format(s, args));
    }
}
