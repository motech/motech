package org.motechproject.osgi.web.exception;

/**
 * Signals an exception with rendering a JSP view.
 */
public class RenderException extends Exception {

    private static final long serialVersionUID = -54535023644311602L;

    public RenderException(String message) {
        super(message);
    }

    public RenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RenderException(Throwable cause) {
        super(cause);
    }
}
