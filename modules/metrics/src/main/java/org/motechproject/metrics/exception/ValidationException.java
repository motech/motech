package org.motechproject.metrics.exception;

import java.util.Map;

/**
 * Exception thrown by metric agent backend implementation
 * when config saving fails
 */

public class ValidationException extends IllegalArgumentException {

    private final String implementationName;
    private final Map<String, String> errors;

    public ValidationException(String implementationName, Map<String, String> errors) {
        this.implementationName = implementationName;
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("There were errors during saving ").append(implementationName).append(" settings:\n");

        for (Map.Entry<String, String> entry : errors.entrySet()) {
            sb.append(" - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return sb.toString();
    }
}
