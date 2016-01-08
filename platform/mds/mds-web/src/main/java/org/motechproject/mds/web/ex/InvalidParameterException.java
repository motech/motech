package org.motechproject.mds.web.ex;

/**
 * Thrown when invalid parameter is passed to {@link org.motechproject.mds.web.rest.ParamParser} class.
 */
public class InvalidParameterException extends RuntimeException {

    private String message;

    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public InvalidParameterException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
