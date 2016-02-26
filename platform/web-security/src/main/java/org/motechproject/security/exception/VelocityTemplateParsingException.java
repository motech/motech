package org.motechproject.security.exception;

/**
 * Thrown when there were problems while parsing velocity template.
 */
public class VelocityTemplateParsingException extends Exception {

    public VelocityTemplateParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
