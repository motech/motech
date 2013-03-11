package org.motechproject.mrs.exception;

import java.lang.IllegalArgumentException;
import java.lang.String;

public class InvalidFacilityException extends IllegalArgumentException {

    public InvalidFacilityException() {
    }

    public InvalidFacilityException(String message) {
        super(message);
    }
}
