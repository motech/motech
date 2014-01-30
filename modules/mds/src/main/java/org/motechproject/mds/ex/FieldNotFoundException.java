package org.motechproject.mds.ex;

/**
 * This exception signals that a given field was not found for the Entity.
 */
public class FieldNotFoundException extends MdsException {

    private static final long serialVersionUID = 4823302726665732484L;

    public FieldNotFoundException() {
        super("mds.error.fieldNotFound");
    }
}
