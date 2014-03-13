package org.motechproject.mds.ex;

/**
 * An exception which signals that a given type does not exist in the database.
 */
public class NoSuchTypeException extends MdsException {

    private static final long serialVersionUID = 8405919984146102956L;

    /**
     * Constructs a new NoSuchTypeException with <i>mds.error.noSuchType</i> as
     * a message key.
     */
    public NoSuchTypeException() {
        super("mds.error.noSuchType");
    }
}
