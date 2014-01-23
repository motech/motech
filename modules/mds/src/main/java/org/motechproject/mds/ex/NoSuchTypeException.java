package org.motechproject.mds.ex;

public class NoSuchTypeException extends MdsException {

    private static final long serialVersionUID = 8405919984146102956L;

    /**
     * Constructs a new NoSuchTypeException with <i>mds.error.noSuchType</i> as
     * a message key.
     */
    public NoSuchTypeException() {
        super("mds.error.noSuchType");
    }}
