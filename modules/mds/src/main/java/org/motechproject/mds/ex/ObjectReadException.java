package org.motechproject.mds.ex;

/**
 * Signals that we were unable to we are unable to parse the object coming from the database.
 */
public class ObjectReadException extends MdsException {

    private static final long serialVersionUID = -6214111291407582493L;

    public ObjectReadException() {
        super("mds.error.objectReadError");
    }

    public ObjectReadException(Throwable cause) {
        super("mds.error.objectReadError", cause);
    }
}
