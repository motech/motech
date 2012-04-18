package org.motechproject.server.pillreminder.api.domain;

public class ValidationException extends RuntimeException{
    public ValidationException(String message) {
        super(message);
    }
}
