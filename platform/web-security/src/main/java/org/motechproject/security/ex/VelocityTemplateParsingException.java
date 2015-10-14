package org.motechproject.security.ex;

/**
 * Thrown when there were problems while parsing velocity template.
 */
public class VelocityTemplateParsingException extends Exception {

    public VelocityTemplateParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
