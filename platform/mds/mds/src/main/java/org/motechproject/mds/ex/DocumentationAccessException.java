package org.motechproject.mds.ex;

/**
 * Signals an error when writing REST documentation to the provided
 * output.
 */
public class DocumentationAccessException extends RuntimeException {

    private static final long serialVersionUID = 8199094018343203616L;

    public DocumentationAccessException(Throwable cause) {
        super("Unable to serve documentation", cause);
    }
}
