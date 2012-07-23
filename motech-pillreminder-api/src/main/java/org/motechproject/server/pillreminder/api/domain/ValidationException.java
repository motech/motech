package org.motechproject.server.pillreminder.api.domain;

/**
 * Input validation errors
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
